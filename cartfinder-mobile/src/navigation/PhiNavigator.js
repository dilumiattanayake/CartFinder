import React from 'react';
import { createStackNavigator } from '@react-navigation/stack';

import PhiDashboardScreen   from '../screens/phi/PhiDashboardScreen';
import VendorScannerScreen  from '../screens/phi/VendorScannerScreen';
import AuditEntryScreen     from '../screens/phi/AuditEntryScreen';
import BadgeIssuerScreen    from '../screens/phi/BadgeIssuerScreen';
import FlaggedQueueScreen   from '../screens/phi/FlaggedQueueScreen';
import SectorDossierScreen  from '../screens/phi/SectorDossierScreen';

const Stack = createStackNavigator();

const PhiNavigator = () => (
  <Stack.Navigator initialRouteName="PhiDashboard">
    <Stack.Screen name="PhiDashboard"   component={PhiDashboardScreen}  options={{ title: 'PHI Dashboard' }} />
    <Stack.Screen name="VendorScanner"  component={VendorScannerScreen} options={{ title: 'QR Scanner' }} />
    <Stack.Screen name="AuditEntry"     component={AuditEntryScreen}    options={{ title: 'Log Audit' }} />
    <Stack.Screen name="BadgeIssuer"    component={BadgeIssuerScreen}   options={{ title: 'Issue Badge' }} />
    <Stack.Screen name="FlaggedQueue"   component={FlaggedQueueScreen}  options={{ title: 'Flagged Vendors' }} />
    <Stack.Screen name="SectorDossier"  component={SectorDossierScreen} options={{ title: 'Sector Archive' }} />
  </Stack.Navigator>
);

export default PhiNavigator;
