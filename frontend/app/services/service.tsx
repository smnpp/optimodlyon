class OptimodApiService {
	private baseUrl: string;

	constructor() {
		this.baseUrl = process.env.NEXT_OPTIMODAPI_URL || 'http://localhost:10500/optimodapi';
	}

	async loadMap(file: File): Promise<any> {
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
			// Process the data as needed
			return data;
		} catch (error) {
			console.error('Fetch error:', error);
			throw error;
		}
	}

	async testApi(): Promise<any> {
		try {
			const response = await fetch(`${this.baseUrl}${'/ActionServlet?action=test'}`);
			if (!response.ok) {
				throw new Error(`Error: ${response.statusText}`);
			}
			const data = await response.json();
			console.log('data:', data);
			return data;
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