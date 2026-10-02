import { Platform, Alert, Linking } from 'react-native';
import { requestLocationPermission } from '../services/locationService';

/**
 * Prompts the user to grant location permission.
 * If denied, shows an alert with a link to device settings.
 */
export const ensureLocationPermission = async () => {
  const granted = await requestLocationPermission();
  if (!granted) {
    Alert.alert(
      'Location Required',
      'CartFinder needs your location to show nearby food carts. Please enable it in Settings.',
      [
        { text: 'Cancel', style: 'cancel' },
        { text: 'Open Settings', onPress: () => Linking.openSettings() },
      ],
    );
  }
  return granted;
};
