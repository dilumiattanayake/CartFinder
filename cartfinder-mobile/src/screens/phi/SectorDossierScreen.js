import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { COLORS } from '../../constants/colors';
import { FONT_SIZES } from '../../constants/typography';

const SectorDossierScreen = () => (
  <View style={styles.container}>
    <Text style={styles.text}>SectorDossierScreen</Text>
    <Text style={styles.sub}>Historical audit archive for sector vendors</Text>
  </View>
);

const styles = StyleSheet.create({
  container: { flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: COLORS.background },
  text:      { fontSize: FONT_SIZES.lg, fontWeight: '700', color: COLORS.text },
  sub:       { fontSize: FONT_SIZES.sm, color: COLORS.textMuted, marginTop: 8 },
});

export default SectorDossierScreen;
