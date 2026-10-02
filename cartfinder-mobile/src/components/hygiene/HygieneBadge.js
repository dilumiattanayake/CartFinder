import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { COLORS } from '../../constants/colors';
import { FONT_SIZES } from '../../constants/typography';

/** Grade badge colour map */
const GRADE_COLORS = {
  A: COLORS.gradeA,
  B: COLORS.gradeB,
  C: COLORS.gradeC,
  D: COLORS.gradeD,
  Unrated: COLORS.textLight,
};

/**
 * Displays an official PHI hygiene grade badge.
 * Props:
 *  - grade: 'A' | 'B' | 'C' | 'D' | 'Unrated'
 *  - size: 'sm' | 'md' (default) | 'lg'
 */
const HygieneBadge = ({ grade = 'Unrated', size = 'md' }) => {
  const dim = { sm: 40, md: 60, lg: 90 }[size];
  const fontSize = { sm: FONT_SIZES.md, md: FONT_SIZES.xl, lg: FONT_SIZES.xxl }[size];

  return (
    <View
      style={[
        styles.badge,
        { width: dim, height: dim, borderRadius: dim / 2, backgroundColor: GRADE_COLORS[grade] || COLORS.textLight },
      ]}
    >
      <Text style={[styles.grade, { fontSize }]}>{grade === 'Unrated' ? '?' : grade}</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  badge: { justifyContent: 'center', alignItems: 'center', elevation: 3 },
  grade: { color: '#FFFFFF', fontWeight: '800' },
});

export default HygieneBadge;
