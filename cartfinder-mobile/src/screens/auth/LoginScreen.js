import React, { useState } from 'react';
import { View, Text, StyleSheet, ScrollView, Alert } from 'react-native';
import AppInput  from '../../components/common/AppInput';
import AppButton from '../../components/common/AppButton';
import { signInWithEmail } from '../../services/authService';
import { COLORS } from '../../constants/colors';
import { FONT_SIZES } from '../../constants/typography';

const LoginScreen = ({ navigation }) => {
  const [email, setEmail]       = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading]   = useState(false);

  const handleLogin = async () => {
    setLoading(true);
    try {
      await signInWithEmail(email.trim(), password);
      // AppNavigator will auto-redirect via onAuthStateChanged
    } catch (err) {
      Alert.alert('Login Failed', err.message);
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
      <AppButton title="Create Account" onPress={() => navigation.navigate('Register')} variant="secondary" style={styles.btn} />
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: { flexGrow: 1, padding: 24, justifyContent: 'center', backgroundColor: COLORS.background },
  title:     { fontSize: FONT_SIZES.xl, fontWeight: '700', color: COLORS.text, marginBottom: 24 },
  btn:       { marginTop: 12 },
});

export default LoginScreen;
