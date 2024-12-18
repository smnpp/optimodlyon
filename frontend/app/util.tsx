export const findClosestPoint = (
    position: google.maps.LatLngLiteral,
    map: { key: number; location: google.maps.LatLngLiteral }[],
): { key: number; location: google.maps.LatLngLiteral } | null => {
    let closestPoint: {
        key: number;
        location: google.maps.LatLngLiteral;
    } | null = null;
    let minDistance = Infinity;

    map.forEach((point) => {
        const distance = getDistance(position, point.location);
        if (distance < minDistance) {
            minDistance = distance;
            closestPoint = point;
        }
    });
    return closestPoint;
};

export const getDistance = (
    pos1: google.maps.LatLngLiteral,
    pos2: google.maps.LatLngLiteral,
): number => {
    const R = 6371e3; // Earth's radius in meters
    const φ1 = (pos1.lat * Math.PI) / 180; // φ, λ in radians
    const φ2 = (pos2.lat * Math.PI) / 180;
    const Δφ = ((pos2.lat - pos1.lat) * Math.PI) / 180;
    const Δλ = ((pos2.lng - pos1.lng) * Math.PI) / 180;

    const a =
        Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
        Math.cos(φ1) * Math.cos(φ2) * Math.sin(Δλ / 2) * Math.sin(Δλ / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    const distance = R * c; // in meters
    return distance;
};
