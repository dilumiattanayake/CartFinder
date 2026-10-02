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
    setLoading(true);
    try {
      await registerWithEmail(email.trim(), password);
      if (selectedRole) await registerInBackend(selectedRole);
    } catch (err) {
      Alert.alert('Registration Failed', err.message);
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
