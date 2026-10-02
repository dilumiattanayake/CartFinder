import React from 'react';
import { Text } from 'react-native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { createStackNavigator } from '@react-navigation/stack';
import { Ionicons } from '@expo/vector-icons';
import { COLORS } from '../constants/colors';

import MapDiscoveryScreen   from '../screens/customer/MapDiscoveryScreen';
import StallDetailScreen    from '../screens/customer/StallDetailScreen';
import MenuScreen           from '../screens/customer/MenuScreen';
import ReviewsScreen        from '../screens/customer/ReviewsScreen';
import SubmitReviewScreen   from '../screens/customer/SubmitReviewScreen';
import HygieneBadgeScreen   from '../screens/customer/HygieneBadgeScreen';
import ComplaintFormScreen  from '../screens/customer/ComplaintFormScreen';
import ProfileScreen        from '../screens/customer/ProfileScreen';
import ProfileSettingsScreen from '../screens/customer/ProfileSettingsScreen';
import HygienePreferencesScreen from '../screens/customer/HygienePreferencesScreen';
import DietaryPreferencesScreen from '../screens/customer/DietaryPreferencesScreen';
import ChangePasswordScreen from '../screens/customer/ChangePasswordScreen';
import {
  MyReviewsScreen,
  SavedCartsScreen,
  MyComplaintsScreen,
  NotificationPreferencesScreen,
} from '../screens/customer/ProfileSubScreens';

const Tab = createBottomTabNavigator();
const MapStack = createStackNavigator();
const ProfileStack = createStackNavigator();

const MapStackNavigator = () => (
  <MapStack.Navigator>
    <MapStack.Screen name="MapDiscovery"   component={MapDiscoveryScreen} options={{ headerShown: false }} />
    <MapStack.Screen name="StallDetail"    component={StallDetailScreen} />
    <MapStack.Screen name="Menu"           component={MenuScreen} />
    <MapStack.Screen name="Reviews"        component={ReviewsScreen} />
    <MapStack.Screen name="SubmitReview"   component={SubmitReviewScreen} />
    <MapStack.Screen name="HygieneBadge"   component={HygieneBadgeScreen} />
    <MapStack.Screen name="ComplaintForm"  component={ComplaintFormScreen} />
  </MapStack.Navigator>
);

const ProfileStackNavigator = () => (
  <ProfileStack.Navigator>
    <ProfileStack.Screen
      name="ProfileHome"
      component={ProfileScreen}
      options={{ headerShown: false }}
    />
    <ProfileStack.Screen
      name="ProfileSettings"
      component={ProfileSettingsScreen}
      options={{ headerShown: false }}
    />
    <ProfileStack.Screen
      name="HygienePreferences"
      component={HygienePreferencesScreen}
      options={{ headerShown: false }}
    />
    <ProfileStack.Screen
      name="DietaryPreferences"
      component={DietaryPreferencesScreen}
      options={{ headerShown: false }}
    />
    <ProfileStack.Screen
      name="ChangePassword"
      component={ChangePasswordScreen}
      options={{ headerShown: false }}
    />
    <ProfileStack.Screen
      name="MyReviews"
      component={MyReviewsScreen}
      options={{ headerShown: false }}
    />
    <ProfileStack.Screen
      name="SavedCarts"
      component={SavedCartsScreen}
      options={{ headerShown: false }}
    />
    <ProfileStack.Screen
      name="MyComplaints"
      component={MyComplaintsScreen}
      options={{ headerShown: false }}
    />
    <ProfileStack.Screen
      name="NotificationPreferences"
      component={NotificationPreferencesScreen}
      options={{ headerShown: false }}
    />
  </ProfileStack.Navigator>
);

const CustomerNavigator = () => (
  <Tab.Navigator
    screenOptions={({ route }) => ({
      headerShown: false,
      tabBarActiveTintColor: COLORS.primary,
      tabBarInactiveTintColor: COLORS.textMuted,
      tabBarStyle: { backgroundColor: COLORS.surface },
      tabBarIcon: ({ focused, color, size }) => {
        let iconName;

        if (route.name === 'Discover') {
          iconName = focused ? 'search' : 'search-outline';
        } else if (route.name === 'Reviews') {
          iconName = focused ? 'star' : 'star-outline';
        } else if (route.name === 'Notifications') {
          iconName = focused ? 'notifications' : 'notifications-outline';
        } else if (route.name === 'Profile') {
          iconName = focused ? 'person' : 'person-outline';
        }

        return <Ionicons name={iconName} size={size} color={color} />;
      },
      tabBarLabel: ({ focused, color }) => {
        const labels = {
          Discover: 'Map',
          Reviews: 'Reviews',
          Notifications: 'Notifications',
          Profile: 'Profile',
        };
        return (
          <Text style={{ color, fontSize: 11, fontWeight: focused ? '600' : '400' }}>
            {labels[route.name]}
          </Text>
        );
      },
    })}
  >
    <Tab.Screen
      name="Discover"
      component={MapStackNavigator}
      options={{
        tabBarLabel: 'Map',
      }}
    />
    <Tab.Screen
      name="Reviews"
      component={ReviewsScreen}
      options={{
        tabBarLabel: 'Reviews',
      }}
    />
    <Tab.Screen
      name="Notifications"
      component={ReviewsScreen} // Placeholder
      options={{
        tabBarLabel: 'Notifications',
      }}
    />
    <Tab.Screen
      name="Profile"
      component={ProfileStackNavigator}
      options={{
        tabBarLabel: 'Profile',
      }}
    />
  </Tab.Navigator>
);

export default CustomerNavigator;
