import React, { useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import AppButton from '../../components/common/AppButton';
import { ROLES, ROLE_LABELS } from '../../constants/roles';
import { COLORS } from '../../constants/colors';
import { FONT_SIZES } from '../../constants/typography';

const SELECTABLE_ROLES = [ROLES.CUSTOMER, ROLES.VENDOR, ROLES.PHI];

const RoleSelectScreen = ({ navigation }) => {
  const [selected, setSelected] = useState(ROLES.CUSTOMER);

  const handleConfirm = () => navigation.navigate('Register', { role: selected });

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Who are you?</Text>
      <Text style={styles.sub}>Select your role to personalise your experience.</Text>
      {SELECTABLE_ROLES.map((role) => (
        <TouchableOpacity
          key={role}
          style={[styles.card, selected === role && styles.cardSelected]}
          onPress={() => setSelected(role)}
        >
          <Text style={[styles.cardText, selected === role && styles.cardTextSelected]}>
            {ROLE_LABELS[role]}
          </Text>
        </TouchableOpacity>
      ))}
      <AppButton title="Continue to registration" onPress={handleConfirm} style={styles.btn} />
    </View>
  );
};

const styles = StyleSheet.create({
  container:        { flex: 1, padding: 24, justifyContent: 'center', backgroundColor: COLORS.background },
  title:            { fontSize: FONT_SIZES.xl, fontWeight: '700', color: COLORS.text, marginBottom: 8 },
  sub:              { fontSize: FONT_SIZES.sm, color: COLORS.textMuted, marginBottom: 24 },
  card:             { borderWidth: 2, borderColor: COLORS.border, borderRadius: 12, padding: 16, marginBottom: 12 },
  cardSelected:     { borderColor: COLORS.primary, backgroundColor: COLORS.primaryLight },
  cardText:         { fontSize: FONT_SIZES.base, color: COLORS.text },
  cardTextSelected: { color: COLORS.primary, fontWeight: '700' },
  btn:              { marginTop: 24 },
});

export default RoleSelectScreen;
