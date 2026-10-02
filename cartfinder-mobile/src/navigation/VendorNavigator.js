import React from 'react';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { COLORS } from '../constants/colors';

import VendorDashboardScreen    from '../screens/vendor/VendorDashboardScreen';
import BroadcastScreen          from '../screens/vendor/BroadcastScreen';
import MenuEditorScreen         from '../screens/vendor/MenuEditorScreen';
import FeedbackScreen           from '../screens/vendor/FeedbackScreen';
import AnalyticsDashboardScreen from '../screens/vendor/AnalyticsDashboardScreen';

const Tab = createBottomTabNavigator();

const VendorNavigator = () => (
  <Tab.Navigator
    screenOptions={{
      headerShown: false,
      tabBarActiveTintColor:   COLORS.primary,
      tabBarInactiveTintColor: COLORS.textMuted,
    }}
  >
    <Tab.Screen name="Dashboard"  component={VendorDashboardScreen} />
    <Tab.Screen name="Broadcast"  component={BroadcastScreen} />
    <Tab.Screen name="Menu"       component={MenuEditorScreen} />
    <Tab.Screen name="Feedback"   component={FeedbackScreen} />
    <Tab.Screen name="Analytics"  component={AnalyticsDashboardScreen} />
  </Tab.Navigator>
);

export default VendorNavigator;
