import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  Image,
  TouchableOpacity,
  Alert,
  ActivityIndicator,
} from 'react-native';
import { useDispatch, useSelector } from 'react-redux';
import { Ionicons, MaterialCommunityIcons } from '@expo/vector-icons';
import { clearAuth } from '../../store/slices/authSlice';
import { signOut } from '../../services/authService';
import { COLORS } from '../../constants/colors';
import { FONT_SIZES } from '../../constants/typography';

const ProfileScreen = ({ navigation }) => {
  const dispatch = useDispatch();
  const { user } = useSelector((s) => s.auth);
  const [loading, setLoading] = useState(false);

  const handleLogout = async () => {
    Alert.alert('Log Out', 'Are you sure you want to log out?', [
      { text: 'Cancel', onPress: () => {}, style: 'cancel' },
      {
        text: 'Log Out',
        onPress: async () => {
          setLoading(true);
          try {
            await signOut();
            dispatch(clearAuth());
          } catch (err) {
            Alert.alert('Error', err.message);
          } finally {
            setLoading(false);
          }
        },
        style: 'destructive',
      },
    ]);
  };

  const stats = [
    { label: 'REVIEWS', value: 54 },
    { label: 'BOOKMARKS', value: 12 },
    { label: 'COMPLAINTS', value: 8 },
  ];

  const menuItems = [
    {
      section: 'HEALTH & SAFETY SETTINGS',
      icon: '🛡️',
      items: [
        { icon: 'checkmark-circle', label: 'Min. Hygiene Rating Level', value: '3.5+', onPress: () => navigation.navigate('HygienePreferences') },
        { icon: 'toggle-switch', label: 'Only Show PHI-Certified Carts', type: 'toggle', value: true, onPress: () => {} },
      ],
    },
    {
      section: 'MY APP ACTIVITY',
      icon: '🍔',
      items: [
        { icon: 'star', label: 'My Reviews & Ratings', onPress: () => navigation.navigate('MyReviews') },
        { icon: 'bookmark', label: 'Saved Carts & Favorites', onPress: () => navigation.navigate('SavedCarts') },
        { icon: 'flag', label: 'Filed Hygiene Concerns', badge: '1 Pending', onPress: () => navigation.navigate('MyComplaints') },
      ],
    },
    {
      section: 'PERSONALIZATION',
      icon: '🎨',
      items: [
        { icon: 'book', label: 'Dietary & Food Preferences', value: 'Halal / Veg', onPress: () => navigation.navigate('DietaryPreferences') },
        { icon: 'notifications', label: 'Notification Preferences', onPress: () => navigation.navigate('NotificationPreferences') },
      ],
    },
    {
      section: 'ACCOUNT ACTIONS',
      icon: '🔐',
      items: [
        { icon: 'key', label: 'Change Password', onPress: () => navigation.navigate('ChangePassword') },
        { icon: 'log-out', label: 'Log Out', onPress: handleLogout, color: COLORS.error },
      ],
    },
  ];

  if (loading) {
    return (
      <View style={styles.centerContainer}>
        <ActivityIndicator size="large" color={COLORS.primary} />
      </View>
    );
  }

  return (
    <ScrollView style={styles.container} showsVerticalScrollIndicator={false}>
      {/* Header with gradient background */}
      <View style={styles.headerContainer}>
        <View style={styles.headerTop}>
          <TouchableOpacity onPress={() => navigation.goBack()}>
            <Ionicons name="chevron-back" size={24} color="#fff" />
          </TouchableOpacity>
          <Text style={styles.headerTitle}>My Profile</Text>
          <TouchableOpacity onPress={() => navigation.navigate('ProfileSettings')}>
            <Ionicons name="settings" size={24} color="#fff" />
          </TouchableOpacity>
        </View>

        {/* Profile Picture */}
        <View style={styles.profileImageContainer}>
          <Image
            source={{
              uri: user?.photoURL || 'https://via.placeholder.com/120',
            }}
            style={styles.profileImage}
          />
          <View style={styles.statusIndicator} />
          <TouchableOpacity style={styles.editButton}>
            <Ionicons name="pencil" size={16} color="#fff" />
          </TouchableOpacity>
        </View>

        {/* Badge */}
        <View style={styles.badgeContainer}>
          <Text style={styles.badgeText}>✨ Foodie Level 3 ✨</Text>
        </View>

        {/* Name */}
        <Text style={styles.nameText}>{user?.displayName || 'User'}</Text>

        {/* Stats */}
        <View style={styles.statsContainer}>
          {stats.map((stat, idx) => (
            <View key={idx} style={styles.statItem}>
              <Text style={styles.statValue}>{stat.value}</Text>
              <Text style={styles.statLabel}>{stat.label}</Text>
            </View>
          ))}
        </View>
      </View>

      {/* Menu Items */}
      <View style={styles.menuContainer}>
        {menuItems.map((section, sectionIdx) => (
          <View key={sectionIdx} style={styles.section}>
            <Text style={styles.sectionTitle}>
              {section.icon} {section.section}
            </Text>
            {section.items.map((item, itemIdx) => (
              <TouchableOpacity
                key={itemIdx}
                style={[styles.menuItem, item.color && { borderLeftColor: item.color, borderLeftWidth: 4 }]}
                onPress={item.onPress}
              >
                <View style={styles.menuItemLeft}>
                  {item.type === 'toggle' ? (
                    <MaterialCommunityIcons name={item.icon} size={24} color={COLORS.primary} />
                  ) : (
                    <Ionicons name={item.icon} size={20} color={item.color || COLORS.primary} />
                  )}
                  <Text style={[styles.menuItemLabel, item.color && { color: item.color }]}>
                    {item.label}
                  </Text>
                </View>
                <View style={styles.menuItemRight}>
                  {item.value && <Text style={styles.menuItemValue}>{item.value}</Text>}
                  {item.badge && <Text style={styles.badgeNotification}>{item.badge}</Text>}
                  {item.type === 'toggle' ? (
                    <View style={[styles.toggle, item.value && styles.toggleActive]} />
                  ) : (
                    <Ionicons name="chevron-forward" size={20} color={COLORS.textMuted} />
                  )}
                </View>
              </TouchableOpacity>
            ))}
          </View>
        ))}
      </View>

      <View style={styles.footer} />
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: COLORS.background,
  },
  centerContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: COLORS.background,
  },
  headerContainer: {
    backgroundColor: COLORS.primary,
    paddingBottom: 32,
    borderBottomLeftRadius: 24,
    borderBottomRightRadius: 24,
  },
  headerTop: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 16,
    paddingTop: 12,
    paddingBottom: 8,
  },
  headerTitle: {
    fontSize: FONT_SIZES.lg,
    fontWeight: '700',
    color: '#fff',
  },
  profileImageContainer: {
    alignItems: 'center',
    marginTop: 16,
    position: 'relative',
  },
  profileImage: {
    width: 120,
    height: 120,
    borderRadius: 60,
    borderWidth: 4,
    borderColor: '#fff',
  },
  statusIndicator: {
    position: 'absolute',
    width: 16,
    height: 16,
    borderRadius: 8,
    backgroundColor: '#4CAF50',
    top: 8,
    right: 8,
    borderWidth: 2,
    borderColor: '#fff',
  },
  editButton: {
    position: 'absolute',
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: COLORS.primary,
    justifyContent: 'center',
    alignItems: 'center',
    bottom: -8,
    right: 0,
    borderWidth: 3,
    borderColor: '#fff',
  },
  badgeContainer: {
    marginTop: 16,
    alignItems: 'center',
  },
  badgeText: {
    paddingHorizontal: 16,
    paddingVertical: 6,
    borderRadius: 20,
    backgroundColor: 'rgba(255,255,255,0.3)',
    color: '#fff',
    fontSize: FONT_SIZES.sm,
    fontWeight: '600',
  },
  nameText: {
    fontSize: FONT_SIZES.xl,
    fontWeight: '700',
    color: '#fff',
    marginTop: 12,
    textAlign: 'center',
  },
  statsContainer: {
    flexDirection: 'row',
    marginTop: 20,
    marginHorizontal: 16,
    backgroundColor: '#fff',
    borderRadius: 16,
    overflow: 'hidden',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 3,
  },
  statItem: {
    flex: 1,
    alignItems: 'center',
    paddingVertical: 16,
    borderRightWidth: 1,
    borderRightColor: '#f0f0f0',
  },
  statValue: {
    fontSize: FONT_SIZES.lg,
    fontWeight: '700',
    color: COLORS.text,
  },
  statLabel: {
    fontSize: FONT_SIZES.xs,
    color: COLORS.textMuted,
    marginTop: 4,
    fontWeight: '600',
  },
  menuContainer: {
    paddingHorizontal: 16,
    paddingTop: 24,
    paddingBottom: 16,
  },
  section: {
    marginBottom: 24,
  },
  sectionTitle: {
    fontSize: FONT_SIZES.sm,
    fontWeight: '700',
    color: COLORS.textMuted,
    marginBottom: 12,
    paddingHorizontal: 4,
  },
  menuItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    backgroundColor: '#fff',
    paddingHorizontal: 16,
    paddingVertical: 14,
    marginBottom: 8,
    borderRadius: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.05,
    shadowRadius: 3,
    elevation: 1,
  },
  menuItemLeft: {
    flexDirection: 'row',
    alignItems: 'center',
    flex: 1,
  },
  menuItemLabel: {
    fontSize: FONT_SIZES.base,
    fontWeight: '500',
    color: COLORS.text,
    marginLeft: 12,
  },
  menuItemRight: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  menuItemValue: {
    fontSize: FONT_SIZES.sm,
    color: COLORS.primary,
    fontWeight: '600',
    marginRight: 8,
    backgroundColor: '#f0f9ff',
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 8,
  },
  badgeNotification: {
    fontSize: FONT_SIZES.xs,
    color: '#fff',
    fontWeight: '700',
    backgroundColor: COLORS.danger,
    paddingHorizontal: 8,
    paddingVertical: 3,
    borderRadius: 6,
    marginRight: 8,
  },
  toggle: {
    width: 44,
    height: 24,
    borderRadius: 12,
    backgroundColor: '#e0e0e0',
  },
  toggleActive: {
    backgroundColor: COLORS.primary,
  },
  footer: {
    height: 32,
  },
});

export default ProfileScreen;
