import React, { useState } from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity, Switch } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { COLORS } from '../../constants/colors';
import { FONT_SIZES } from '../../constants/typography';

const HygienePreferencesScreen = ({ navigation }) => {
  const [minRating, setMinRating] = useState(3.5);
  const [phiCertifiedOnly, setPhiCertifiedOnly] = useState(true);

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()}>
          <Ionicons name="chevron-back" size={24} color={COLORS.text} />
        </TouchableOpacity>
        <Text style={styles.title}>Hygiene Preferences</Text>
        <View style={{ width: 24 }} />
      </View>

      <View style={styles.content}>
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Minimum Hygiene Rating</Text>
          <View style={styles.settingItem}>
            <View>
              <Text style={styles.label}>Show only carts with rating ≥</Text>
              <Text style={styles.description}>3.0, 3.5, 4.0, 4.5, 5.0</Text>
            </View>
            <Text style={styles.value}>{minRating}+</Text>
          </View>

          <View style={styles.ratingButtons}>
            {[3.0, 3.5, 4.0, 4.5, 5.0].map((rating) => (
              <TouchableOpacity
                key={rating}
                style={[styles.ratingButton, minRating === rating && styles.ratingButtonActive]}
                onPress={() => setMinRating(rating)}
              >
                <Text
                  style={[
                    styles.ratingButtonText,
                    minRating === rating && styles.ratingButtonTextActive,
                  ]}
                >
                  {rating}
                </Text>
              </TouchableOpacity>
            ))}
          </View>
        </View>

        <View style={styles.section}>
          <View style={styles.settingItem}>
            <View>
              <Text style={styles.label}>Only Show PHI-Certified Carts</Text>
              <Text style={styles.description}>
                Only display vendors who have been verified by health inspectors
              </Text>
            </View>
            <Switch
              value={phiCertifiedOnly}
              onValueChange={setPhiCertifiedOnly}
              trackColor={{ false: '#ddd', true: COLORS.primary + '50' }}
              thumbColor={phiCertifiedOnly ? COLORS.primary : '#ccc'}
            />
          </View>
        </View>

        <View style={styles.infoBox}>
          <Ionicons name="information-circle" size={20} color={COLORS.primary} />
          <Text style={styles.infoText}>
            These preferences help us show you the safest and most trusted food carts in your area.
          </Text>
        </View>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: COLORS.background,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 16,
    paddingVertical: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  title: {
    fontSize: FONT_SIZES.lg,
    fontWeight: '700',
    color: COLORS.text,
  },
  content: {
    padding: 16,
  },
  section: {
    marginBottom: 24,
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
  },
  sectionTitle: {
    fontSize: FONT_SIZES.base,
    fontWeight: '700',
    color: COLORS.text,
    marginBottom: 16,
  },
  settingItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 12,
  },
  label: {
    fontSize: FONT_SIZES.base,
    fontWeight: '600',
    color: COLORS.text,
  },
  description: {
    fontSize: FONT_SIZES.xs,
    color: COLORS.textMuted,
    marginTop: 4,
  },
  value: {
    fontSize: FONT_SIZES.lg,
    fontWeight: '700',
    color: COLORS.primary,
    backgroundColor: '#f0f9ff',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 8,
  },
  ratingButtons: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: 12,
  },
  ratingButton: {
    flex: 1,
    paddingVertical: 10,
    marginHorizontal: 4,
    borderWidth: 2,
    borderColor: '#ddd',
    borderRadius: 8,
    alignItems: 'center',
  },
  ratingButtonActive: {
    borderColor: COLORS.primary,
    backgroundColor: '#f0f9ff',
  },
  ratingButtonText: {
    fontSize: FONT_SIZES.sm,
    fontWeight: '600',
    color: COLORS.textMuted,
  },
  ratingButtonTextActive: {
    color: COLORS.primary,
  },
  infoBox: {
    flexDirection: 'row',
    backgroundColor: '#f0f9ff',
    borderRadius: 12,
    padding: 12,
    alignItems: 'flex-start',
  },
  infoText: {
    fontSize: FONT_SIZES.sm,
    color: COLORS.text,
    marginLeft: 12,
    flex: 1,
  },
});

export default HygienePreferencesScreen;
