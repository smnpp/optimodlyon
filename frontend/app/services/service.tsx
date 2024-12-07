import Intersection from '../types/intersection';

class OptimodApiService {
	private baseUrl: string;

	constructor() {
		this.baseUrl = process.env.NEXT_OPTIMODAPI_URL || 'http://localhost:10500/optimodapi';
	}

	async loadMap(file: File): Promise<Intersection[]> {
		const fileName = file.name;
		const fileContent = await this.readFileContent(file);

		const body = {
			"file-name": fileName,
			"file-content": fileContent,
		};
		console.log('body:', body);

		try {
			const response = await fetch(`${this.baseUrl}${'/ActionServlet?action=load-map'}`, {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json',
				},
				body: JSON.stringify(body),
			});
			if (!response.ok) {
				throw new Error(`Error: ${response.statusText}`);
			}
			const data = await response.json();
			console.log('data:', data);
			const map = data.map;

			console.log('map:', map);

			const intersections: Intersection[] = data.map.map((item: { id: string; location: { latitude: number; longitude: number } }) => {
				const latitude = item.location.latitude;
				const longitude = item.location.longitude;
			
				const location: google.maps.LatLngLiteral = {
					lat: latitude,
					lng: longitude
				};
			
				return {
					key: item.id,
					location: location
				};
			});

			return intersections;
		} catch (error) {
			console.error('Fetch error:', error);
			throw error;
		}
	}
	
	private readFileContent(file: File): Promise<string> {
		return new Promise((resolve, reject) => {
			const reader = new FileReader();
			reader.onload = (event) => {
				resolve(event.target?.result as string);
			};
			reader.onerror = (error) => {
				reject(error);
			};
			reader.readAsText(file);
		});
	}
}

export default OptimodApiService;