import React from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { createStackNavigator } from '@react-navigation/stack';
import { COLORS } from '../constants/colors';

import MapDiscoveryScreen   from '../screens/customer/MapDiscoveryScreen';
import StallDetailScreen    from '../screens/customer/StallDetailScreen';
import MenuScreen           from '../screens/customer/MenuScreen';
import ReviewsScreen        from '../screens/customer/ReviewsScreen';
import SubmitReviewScreen   from '../screens/customer/SubmitReviewScreen';
import HygieneBadgeScreen   from '../screens/customer/HygieneBadgeScreen';
import ComplaintFormScreen  from '../screens/customer/ComplaintFormScreen';

const Tab = createBottomTabNavigator();
const MapStack = createStackNavigator();

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

const CustomerNavigator = () => (
  <Tab.Navigator
    screenOptions={{
      headerShown: false,
      tabBarActiveTintColor:   COLORS.primary,
      tabBarInactiveTintColor: COLORS.textMuted,
      tabBarStyle: { backgroundColor: COLORS.surface },
    }}
  >
    <Tab.Screen name="Discover" component={MapStackNavigator} />
  </Tab.Navigator>
);

export default CustomerNavigator;
