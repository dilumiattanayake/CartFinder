import * as Location from 'expo-location';

/**
 * Request device location permission (iOS + Android).
 * @returns {Promise<boolean>} true if granted
 */
export const requestLocationPermission = async () => {
  const { status } = await Location.requestForegroundPermissionsAsync();
  return status === Location.PermissionStatus.GRANTED;
};

/**
 * Get the device's current GPS position once.
 * @returns {Promise<{ latitude, longitude }>}
 */
export const getCurrentPosition = async () => {
  const { coords } = await Location.getCurrentPositionAsync({
    accuracy: Location.Accuracy.High,
  });
  return { latitude: coords.latitude, longitude: coords.longitude };
};

/**
 * Watch device GPS position continuously.
 * @param {function} onUpdate - Called with { latitude, longitude } on each update
 * @param {function} onError  - Called on error
 * @returns {number} watchId — pass to clearWatch() to stop
 */
export const watchPosition = (onUpdate, onError) =>
  Location.watchPositionAsync(
    { accuracy: Location.Accuracy.High, distanceInterval: 10 },
    ({ coords }) => onUpdate({ latitude: coords.latitude, longitude: coords.longitude }),
  ).catch(onError);

export const clearWatch = (watchSubscription) => watchSubscription?.remove();
