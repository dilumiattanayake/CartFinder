import React, { useState } from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { COLORS } from '../../constants/colors';
import { FONT_SIZES } from '../../constants/typography';

const DietaryPreferencesScreen = ({ navigation }) => {
  const [preferences, setPreferences] = useState({
    halal: true,
    vegan: false,
    vegetarian: true,
    glutenFree: false,
    dairyFree: false,
    spicyFree: false,
  });

  const options = [
    { key: 'halal', label: '🕌 Halal' },
    { key: 'vegan', label: '🌱 Vegan' },
    { key: 'vegetarian', label: '🥗 Vegetarian' },
    { key: 'glutenFree', label: '🌾 Gluten Free' },
    { key: 'dairyFree', label: '🥛 Dairy Free' },
    { key: 'spicyFree', label: '🌶️ Mild (No Spice)' },
  ];

  const togglePreference = (key) => {
    setPreferences({
      ...preferences,
      [key]: !preferences[key],
    });
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()}>
          <Ionicons name="chevron-back" size={24} color={COLORS.text} />
        </TouchableOpacity>
        <Text style={styles.title}>Dietary Preferences</Text>
        <View style={{ width: 24 }} />
      </View>

      <View style={styles.content}>
        <Text style={styles.description}>
          Select your dietary preferences to help us recommend the best food carts for you.
        </Text>

        <View style={styles.grid}>
          {options.map((option) => (
            <TouchableOpacity
              key={option.key}
              style={[styles.card, preferences[option.key] && styles.cardActive]}
              onPress={() => togglePreference(option.key)}
            >
              <Text style={styles.cardIcon}>{option.label.split(' ')[0]}</Text>
              <Text style={[styles.cardLabel, preferences[option.key] && styles.cardLabelActive]}>
                {option.label.split(' ').slice(1).join(' ')}
              </Text>
              {preferences[option.key] && (
                <View style={styles.checkmark}>
                  <Ionicons name="checkmark" size={16} color="#fff" />
                </View>
              )}
            </TouchableOpacity>
          ))}
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
  description: {
    fontSize: FONT_SIZES.sm,
    color: COLORS.textMuted,
    marginBottom: 20,
    lineHeight: 20,
  },
  grid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
  },
  card: {
    width: '48%',
    aspectRatio: 1,
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 12,
    marginBottom: 12,
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 2,
    borderColor: '#e0e0e0',
    position: 'relative',
  },
  cardActive: {
    borderColor: COLORS.primary,
    backgroundColor: '#f0f9ff',
  },
  cardIcon: {
    fontSize: 32,
    marginBottom: 8,
  },
  cardLabel: {
    fontSize: FONT_SIZES.sm,
    fontWeight: '600',
    color: COLORS.text,
    textAlign: 'center',
  },
  cardLabelActive: {
    color: COLORS.primary,
  },
  checkmark: {
    position: 'absolute',
    top: 8,
    right: 8,
    width: 24,
    height: 24,
    borderRadius: 12,
    backgroundColor: COLORS.primary,
    justifyContent: 'center',
    alignItems: 'center',
  },
});

export default DietaryPreferencesScreen;
