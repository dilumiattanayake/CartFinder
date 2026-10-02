import React from 'react';
import { View, TouchableOpacity, Text, StyleSheet } from 'react-native';
import { COLORS } from '../../constants/colors';
import { FONT_SIZES } from '../../constants/typography';

/**
 * Interactive or display-only star rating widget.
 * Props:
 *  - rating: number (0–5)
 *  - onRate: function (called with new rating) — if provided, stars are interactive
 *  - size: number (font size)
 */
const StarRating = ({ rating = 0, onRate, size = FONT_SIZES.lg }) => (
  <View style={styles.row}>
    {[1, 2, 3, 4, 5].map((star) => (
      <TouchableOpacity
        key={star}
        onPress={() => onRate && onRate(star)}
        disabled={!onRate}
        activeOpacity={0.7}
      >
        <Text style={{ fontSize: size, color: star <= rating ? COLORS.warning : COLORS.border }}>
          {star <= rating ? '★' : '☆'}
        </Text>
      </TouchableOpacity>
    ))}
  </View>
);

const styles = StyleSheet.create({
  row: { flexDirection: 'row', alignItems: 'center' },
});

export default StarRating;
