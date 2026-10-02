import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { Marker } from 'react-native-maps';
import { COLORS } from '../../constants/colors';

/**
 * Custom map marker for a vendor / cart.
 * Green = live, grey = offline.
 */
const VendorMapMarker = ({ vendor, onPress }) => {
  const { location, stallName, isLive } = vendor;
  const [lng, lat] = location.coordinates;

  return (
    <Marker
      coordinate={{ latitude: lat, longitude: lng }}
      onPress={() => onPress && onPress(vendor)}
      title={stallName}
    >
      <View style={[styles.pin, { backgroundColor: isLive ? COLORS.markerLive : COLORS.markerOffline }]}>
        <Text style={styles.emoji}>🛒</Text>
      </View>
    </Marker>
  );
};

const styles = StyleSheet.create({
  pin: {
    width: 40, height: 40, borderRadius: 20,
    justifyContent: 'center', alignItems: 'center',
    elevation: 4, shadowColor: '#000', shadowOpacity: 0.3, shadowRadius: 4,
  },
  emoji: { fontSize: 20 },
});

export default VendorMapMarker;
