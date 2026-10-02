import React from 'react';
import { View, Text, Switch, StyleSheet } from 'react-native';
import { COLORS } from '../../constants/colors';
import { FONT_SIZES } from '../../constants/typography';

/**
 * Animated "I'm Here Now" live broadcast toggle switch.
 */
const LiveToggle = ({ isLive, onToggle, disabled = false }) => (
  <View style={styles.row}>
    <View>
      <Text style={styles.label}>{isLive ? "🟢 You're Live!" : '⚫ Offline'}</Text>
      <Text style={styles.sub}>{isLive ? 'Customers can find you on the map' : 'Tap to broadcast your location'}</Text>
    </View>
    <Switch
      value={isLive}
      onValueChange={onToggle}
      disabled={disabled}
      trackColor={{ false: COLORS.border, true: COLORS.success }}
      thumbColor={isLive ? COLORS.surface : COLORS.textLight}
    />
  </View>
);

const styles = StyleSheet.create({
  row:   { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', padding: 16 },
  label: { fontSize: FONT_SIZES.md, fontWeight: '700', color: COLORS.text },
  sub:   { fontSize: FONT_SIZES.sm, color: COLORS.textMuted, marginTop: 2 },
});

export default LiveToggle;
