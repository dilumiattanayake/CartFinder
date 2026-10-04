import { getApp, getApps, initializeApp } from 'firebase/app';
import {
  getAuth,
  getReactNativePersistence,
  initializeAuth,
} from 'firebase/auth';
import AsyncStorage from '@react-native-async-storage/async-storage';

const firebaseConfig = {
  apiKey: process.env.EXPO_PUBLIC_FIREBASE_API_KEY || 'AIzaSyDIsdTPImAfmQR46p3ySu60wbKyQ-MB-4E',
  authDomain: process.env.EXPO_PUBLIC_FIREBASE_AUTH_DOMAIN || 'cartfinder-3d381.firebaseapp.com',
  projectId: process.env.EXPO_PUBLIC_FIREBASE_PROJECT_ID || 'cartfinder-3d381',
  storageBucket: process.env.EXPO_PUBLIC_FIREBASE_STORAGE_BUCKET || 'cartfinder-3d381.firebasestorage.app',
  messagingSenderId: process.env.EXPO_PUBLIC_FIREBASE_MESSAGING_SENDER_ID || '859998610814',
  appId: process.env.EXPO_PUBLIC_FIREBASE_APP_ID || '1:859998610814:web:fd42eb8aa286e6d53e3f2f',
};

const isConfigured = Boolean(
  firebaseConfig.apiKey && firebaseConfig.projectId && firebaseConfig.appId,
);

export const firebaseAuth = isConfigured
  ? (() => {
      const app = getApps().length ? getApp() : initializeApp(firebaseConfig);
      try {
        return initializeAuth(app, {
          persistence: getReactNativePersistence(AsyncStorage),
        });
      } catch (_error) {
        return getAuth(app);
      }
    })()
  : null;
