import React, { useEffect } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { Provider } from 'react-redux';
import { PersistGate } from 'redux-persist/integration/react';
import { store, persistor } from '../store';
import { useDispatch } from 'react-redux';
import { loadUserProfile, clearAuth, setFirebaseUser } from '../store/slices/authSlice';
import { useAuth } from '../hooks/useAuth';
import { firebaseAuth } from '../config/firebase';

import AuthNavigator    from './AuthNavigator';
import CustomerNavigator from './CustomerNavigator';
import VendorNavigator  from './VendorNavigator';
import PhiNavigator     from './PhiNavigator';
import LoadingOverlay   from '../components/common/LoadingOverlay';

const Stack = createStackNavigator();

/** Inner navigator — consumes Redux, decides which navigator to show. */
const RootNavigator = () => {
  const dispatch = useDispatch();
  const { isSignedIn, role, isLoading } = useAuth();

  useEffect(() => {
    if (!firebaseAuth) {
      dispatch(clearAuth());
      return undefined;
    }

    const unsubscribe = firebaseAuth.onAuthStateChanged(async (firebaseUser) => {
      if (firebaseUser) {
        dispatch(setFirebaseUser(firebaseUser));
        dispatch(loadUserProfile());
      } else {
        dispatch(clearAuth());
      }
    });
    return unsubscribe; // cleanup on unmount
  }, [dispatch]);

  if (isLoading) return <LoadingOverlay />;

  // If Firebase user is authenticated but MongoDB registration is incomplete
  if (isSignedIn && !role) {
    return <AuthNavigator />;
  }

  if (!isSignedIn) return <AuthNavigator />;

  switch (role) {
    case 'vendor':   return <VendorNavigator />;
    case 'phi':
    case 'admin':    return <PhiNavigator />;
    case 'customer': return <CustomerNavigator />;
    default:         return <AuthNavigator />;
  }
};

/** Root component — wraps everything in Redux + Navigation providers. */
const AppNavigator = () => (
  <Provider store={store}>
    <PersistGate loading={<LoadingOverlay />} persistor={persistor}>
      <NavigationContainer>
        <RootNavigator />
      </NavigationContainer>
    </PersistGate>
  </Provider>
);

export default AppNavigator;
