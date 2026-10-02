import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { COLORS } from '../../constants/colors';
import { FONT_SIZES } from '../../constants/typography';

const MenuEditorScreen = () => (
  <View style={styles.container}>
    <Text style={styles.text}>MenuEditorScreen</Text>
    <Text style={styles.sub}>Add, edit and remove menu dishes</Text>
  </View>
);

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: COLORS.background },
  text:      { fontSize: FONT_SIZES.lg, fontWeight: '700', color: COLORS.text },
  sub:       { fontSize: FONT_SIZES.sm, color: COLORS.textMuted, marginTop: 8 },
});

export default MenuEditorScreen;
