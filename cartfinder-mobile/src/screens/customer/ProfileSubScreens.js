import React from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity, FlatList } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { COLORS } from '../../constants/colors';
import { FONT_SIZES } from '../../constants/typography';

const EmptyState = ({ icon, title, message }) => (
  <View style={styles.emptyContainer}>
    <Text style={styles.emptyIcon}>{icon}</Text>
    <Text style={styles.emptyTitle}>{title}</Text>
    <Text style={styles.emptyMessage}>{message}</Text>
  </View>
);

const MyReviewsScreen = ({ navigation }) => (
  <ScrollView style={styles.container}>
    <View style={styles.header}>
      <TouchableOpacity onPress={() => navigation.goBack()}>
        <Ionicons name="chevron-back" size={24} color={COLORS.text} />
      </TouchableOpacity>
      <Text style={styles.title}>My Reviews & Ratings</Text>
      <View style={{ width: 24 }} />
    </View>
    <EmptyState
      icon="⭐"
      title="No Reviews Yet"
      message="Your reviews will appear here. Start by reviewing your favorite food carts!"
    />
  </ScrollView>
);

const SavedCartsScreen = ({ navigation }) => (
  <ScrollView style={styles.container}>
    <View style={styles.header}>
      <TouchableOpacity onPress={() => navigation.goBack()}>
        <Ionicons name="chevron-back" size={24} color={COLORS.text} />
      </TouchableOpacity>
      <Text style={styles.title}>Saved Carts & Favorites</Text>
      <View style={{ width: 24 }} />
    </View>
    <EmptyState
      icon="🔖"
      title="No Saved Carts"
      message="Tap the bookmark icon on any cart to save it for quick access!"
    />
  </ScrollView>
);

const MyComplaintsScreen = ({ navigation }) => (
  <ScrollView style={styles.container}>
    <View style={styles.header}>
      <TouchableOpacity onPress={() => navigation.goBack()}>
        <Ionicons name="chevron-back" size={24} color={COLORS.text} />
      </TouchableOpacity>
      <Text style={styles.title}>Filed Hygiene Concerns</Text>
      <View style={{ width: 24 }} />
    </View>
    <View style={styles.content}>
      <View style={styles.complaintItem}>
        <View style={styles.statusBadge}>
          <Text style={styles.statusBadgeText}>PENDING</Text>
        </View>
        <View style={styles.complaintDetails}>
          <Text style={styles.complaintTitle}>Cart Name Here</Text>
          <Text style={styles.complaintDescription}>Hygiene concern filed</Text>
          <Text style={styles.complaintDate}>1 day ago</Text>
        </View>
        <Ionicons name="chevron-forward" size={20} color={COLORS.textMuted} />
      </View>
    </View>
  </ScrollView>
);

const NotificationPreferencesScreen = ({ navigation }) => (
  <ScrollView style={styles.container}>
    <View style={styles.header}>
      <TouchableOpacity onPress={() => navigation.goBack()}>
        <Ionicons name="chevron-back" size={24} color={COLORS.text} />
      </TouchableOpacity>
      <Text style={styles.title}>Notification Preferences</Text>
      <View style={{ width: 24 }} />
    </View>
    <View style={styles.content}>
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Push Notifications</Text>
        <View style={styles.preferenceItem}>
          <Text style={styles.preferenceLabel}>Review Reminders</Text>
          <Ionicons name="toggle" size={24} color={COLORS.primary} />
        </View>
        <View style={styles.preferenceItem}>
          <Text style={styles.preferenceLabel}>New Cart Nearby</Text>
          <Ionicons name="toggle" size={24} color={COLORS.primary} />
        </View>
        <View style={styles.preferenceItem}>
          <Text style={styles.preferenceLabel}>Hygiene Updates</Text>
          <Ionicons name="toggle" size={24} color={COLORS.primary} />
        </View>
      </View>
    </View>
  </ScrollView>
);

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
  emptyContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingVertical: 60,
  },
  emptyIcon: {
    fontSize: 64,
    marginBottom: 16,
  },
  emptyTitle: {
    fontSize: FONT_SIZES.lg,
    fontWeight: '700',
    color: COLORS.text,
    marginBottom: 8,
  },
  emptyMessage: {
    fontSize: FONT_SIZES.base,
    color: COLORS.textMuted,
    textAlign: 'center',
    paddingHorizontal: 32,
    lineHeight: 20,
  },
  complaintItem: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
    marginBottom: 12,
  },
  statusBadge: {
    backgroundColor: '#fff3cd',
    borderRadius: 6,
    paddingHorizontal: 8,
    paddingVertical: 4,
    marginRight: 12,
  },
  statusBadgeText: {
    fontSize: FONT_SIZES.xs,
    fontWeight: '700',
    color: '#856404',
  },
  complaintDetails: {
    flex: 1,
  },
  complaintTitle: {
    fontSize: FONT_SIZES.base,
    fontWeight: '600',
    color: COLORS.text,
  },
  complaintDescription: {
    fontSize: FONT_SIZES.sm,
    color: COLORS.textMuted,
    marginTop: 4,
  },
  complaintDate: {
    fontSize: FONT_SIZES.xs,
    color: COLORS.textMuted,
    marginTop: 4,
  },
  section: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
    marginBottom: 16,
  },
  sectionTitle: {
    fontSize: FONT_SIZES.base,
    fontWeight: '700',
    color: COLORS.text,
    marginBottom: 16,
  },
  preferenceItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  preferenceLabel: {
    fontSize: FONT_SIZES.base,
    fontWeight: '500',
    color: COLORS.text,
  },
});

export { MyReviewsScreen, SavedCartsScreen, MyComplaintsScreen, NotificationPreferencesScreen };
