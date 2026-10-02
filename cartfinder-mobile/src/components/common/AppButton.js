import React from 'react';
import { TouchableOpacity, Text, StyleSheet, ActivityIndicator } from 'react-native';
import { COLORS } from '../../constants/colors';
import { FONT_SIZES } from '../../constants/typography';

/**
 * Primary / secondary themed button.
 * Props:
 *  - title: string
 *  - onPress: function
 *  - variant: 'primary' (default) | 'secondary' | 'danger'
 *  - loading: boolean
 *  - disabled: boolean
 *  - style: ViewStyle overrides
 */
const AppButton = ({ title, onPress, variant = 'primary', loading = false, disabled = false, style }) => {
  const bgColor = {
    primary:   COLORS.primary,
    secondary: COLORS.secondary,
    danger:    COLORS.error,
  }[variant];

  return (
    <TouchableOpacity
      onPress={onPress}
      disabled={disabled || loading}
      style={[styles.button, { backgroundColor: bgColor, opacity: disabled ? 0.6 : 1 }, style]}
      activeOpacity={0.8}
    >
      {loading
        ? <ActivityIndicator color="#fff" />
        : <Text style={styles.text}>{title}</Text>
      }
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  button: {
    paddingVertical:   14,
    paddingHorizontal: 24,
    borderRadius:      10,
    alignItems:        'center',
    justifyContent:    'center',
  },
  text: {
    color:      '#FFFFFF',
    fontSize:   FONT_SIZES.base,
    fontWeight: '600',
  },
});

export default AppButton;
