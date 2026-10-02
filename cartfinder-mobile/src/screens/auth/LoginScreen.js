import React, { useState } from 'react';
import { View, Text, StyleSheet, ScrollView, Alert } from 'react-native';
import { useDispatch } from 'react-redux';
import AppInput  from '../../components/common/AppInput';
import AppButton from '../../components/common/AppButton';
import { signInWithEmail } from '../../services/authService';
import { loadUserProfile } from '../../store/slices/authSlice';
import { COLORS } from '../../constants/colors';
import { FONT_SIZES } from '../../constants/typography';

const LoginScreen = ({ navigation }) => {
  const dispatch = useDispatch();
  const [email, setEmail]       = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading]   = useState(false);

  const handleLogin = async () => {
    if (!email.trim() || !password) {
      Alert.alert('Validation Error', 'Please enter both email and password');
      return;
    }

    setLoading(true);
    try {
      console.log('[LoginScreen] Signing in with email:', email);
      await signInWithEmail(email.trim(), password);
      console.log('[LoginScreen] Firebase login successful');
      
      // Load user profile from MongoDB
      console.log('[LoginScreen] Loading user profile...');
      await dispatch(loadUserProfile()).unwrap();
      console.log('[LoginScreen] User profile loaded, AppNavigator will auto-redirect');
      
      Alert.alert('Login Successful', 'Redirecting to your dashboard...');
    } catch (err) {
      console.error('[LoginScreen] Login error:', err);
      const errorMessage = err?.message || 'Login failed. Please try again.';
      Alert.alert('Login Failed', errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.title}>Welcome Back</Text>
      <AppInput label="Email" value={email} onChangeText={setEmail} keyboardType="email-address" placeholder="you@example.com" />
      <AppInput label="Password" value={password} onChangeText={setPassword} secureTextEntry placeholder="••••••••" />
      <AppButton title="Log In" onPress={handleLogin} loading={loading} style={styles.btn} />
      <AppButton title="Create Account" onPress={() => navigation.navigate('RoleSelect')} variant="secondary" style={styles.btn} />
    </ScrollView>
  );

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.title}>Welcome Back</Text>
      <AppInput label="Email" value={email} onChangeText={setEmail} keyboardType="email-address" placeholder="you@example.com" />
      <AppInput label="Password" value={password} onChangeText={setPassword} secureTextEntry placeholder="••••••••" />
      <AppButton title="Log In" onPress={handleLogin} loading={loading} style={styles.btn} />
      <AppButton title="Create Account" onPress={() => navigation.navigate('RoleSelect')} variant="secondary" style={styles.btn} />
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: { flexGrow: 1, padding: 24, justifyContent: 'center', backgroundColor: COLORS.background },
  title:     { fontSize: FONT_SIZES.xl, fontWeight: '700', color: COLORS.text, marginBottom: 24 },
  btn:       { marginTop: 12 },
});

export default LoginScreen;
