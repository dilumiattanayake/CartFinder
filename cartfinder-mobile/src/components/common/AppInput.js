import React from 'react';
import { View, TextInput, Text, StyleSheet } from 'react-native';
import { COLORS } from '../../constants/colors';
import { FONT_SIZES } from '../../constants/typography';

/**
 * Controlled text input with label and optional error message.
 */
const AppInput = ({
  label, value, onChangeText, placeholder,
  secureTextEntry = false, error, keyboardType = 'default',
  multiline = false, style,
}) => (
  <View style={[styles.wrapper, style]}>
    {label && <Text style={styles.label}>{label}</Text>}
    <TextInput
      value={value}
      onChangeText={onChangeText}
      placeholder={placeholder}
      placeholderTextColor={COLORS.textLight}
      secureTextEntry={secureTextEntry}
      keyboardType={keyboardType}
      multiline={multiline}
      style={[
        styles.input,
        multiline && styles.multiline,
        error && styles.inputError,
      ]}
    />
    {error && <Text style={styles.error}>{error}</Text>}
  </View>
);

const styles = StyleSheet.create({
  wrapper:    { marginBottom: 16 },
  label:      { fontSize: FONT_SIZES.sm, color: COLORS.text, marginBottom: 6, fontWeight: '500' },
  input:      {
    borderWidth: 1, borderColor: COLORS.border, borderRadius: 8,
    padding: 12, fontSize: FONT_SIZES.base, color: COLORS.text,
    backgroundColor: COLORS.surface,
  },
  multiline:  { minHeight: 100, textAlignVertical: 'top' },
  inputError: { borderColor: COLORS.error },
  error:      { fontSize: FONT_SIZES.xs, color: COLORS.error, marginTop: 4 },
});

export default AppInput;
