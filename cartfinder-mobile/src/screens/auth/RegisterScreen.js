import React, { useState } from 'react';
import { View, Text, StyleSheet, ScrollView, Alert } from 'react-native';
import AppInput  from '../../components/common/AppInput';
import AppButton from '../../components/common/AppButton';
import { registerWithEmail, registerInBackend } from '../../services/authService';
import { COLORS } from '../../constants/colors';
import { FONT_SIZES } from '../../constants/typography';

const RegisterScreen = ({ navigation, route }) => {
  const [email, setEmail]       = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading]   = useState(false);
  const selectedRole = route.params?.role;

  const handleRegister = async () => {
    if (!email.trim() || !password) {
      Alert.alert('Validation Error', 'Please enter both email and password');
      return;
    }

    if (!selectedRole) {
      Alert.alert('Selection Error', 'Please select a role');
      navigation.navigate('RoleSelect');
      return;
    }

    setLoading(true);
    try {
      console.log('[RegisterScreen] Starting registration for role:', selectedRole);
      
      // Step 1: Create Firebase user
      console.log('[RegisterScreen] Creating Firebase user...');
      const firebaseResult = await registerWithEmail(email.trim(), password);
      console.log('[RegisterScreen] Firebase user created:', firebaseResult.user.uid);
      
      // Step 2: Register in MongoDB backend
      console.log('[RegisterScreen] Registering in backend...');
      const backendResult = await registerInBackend(selectedRole);
      console.log('[RegisterScreen] Backend registration response:', backendResult);
      
      Alert.alert('Registration Successful', 'Your account has been created! Please log in to continue.');
      
      // Navigate to Login screen
      navigation.navigate('Login');
    } catch (err) {
      console.error('[RegisterScreen] Registration error:', err);
      const errorMessage = err?.message || err?.response?.data?.message || JSON.stringify(err) || 'Registration failed';
      console.error('[RegisterScreen] Error message:', errorMessage);
      Alert.alert('Registration Failed', errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.title}>Create Account</Text>
      <AppInput label="Email" value={email} onChangeText={setEmail} keyboardType="email-address" placeholder="you@example.com" />
      <AppInput label="Password" value={password} onChangeText={setPassword} secureTextEntry placeholder="Min 8 characters" />
      <AppButton title="Register" onPress={handleRegister} loading={loading} style={styles.btn} />
      <AppButton title="Already have an account? Log in" onPress={() => navigation.navigate('Login')} variant="secondary" style={styles.btn} />
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: { flexGrow: 1, padding: 24, justifyContent: 'center', backgroundColor: COLORS.background },
  title:     { fontSize: FONT_SIZES.xl, fontWeight: '700', color: COLORS.text, marginBottom: 24 },
  btn:       { marginTop: 12 },
});

export default RegisterScreen;
