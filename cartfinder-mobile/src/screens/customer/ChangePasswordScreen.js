import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  TextInput,
  Alert,
  ActivityIndicator,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import AppButton from '../../components/common/AppButton';
import { sendPasswordReset } from '../../services/authService';
import { useSelector } from 'react-redux';
import { COLORS } from '../../constants/colors';
import { FONT_SIZES } from '../../constants/typography';

const ChangePasswordScreen = ({ navigation }) => {
  const { user } = useSelector((s) => s.auth);
  const [loading, setLoading] = useState(false);
  const [step, setStep] = useState('confirm'); // confirm or reset

  const handleSendReset = async () => {
    setLoading(true);
    try {
      if (!user?.email) throw new Error('User email not found');
      await sendPasswordReset(user.email);
      Alert.alert(
        'Password Reset Email Sent',
        `Check your email (${user.email}) for instructions to reset your password.`,
        [
          {
            text: 'OK',
            onPress: () => navigation.goBack(),
          },
        ]
      );
    } catch (err) {
      Alert.alert('Error', err.message || 'Failed to send reset email');
    } finally {
      setLoading(false);
    }
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()}>
          <Ionicons name="chevron-back" size={24} color={COLORS.text} />
        </TouchableOpacity>
        <Text style={styles.title}>Change Password</Text>
        <View style={{ width: 24 }} />
      </View>

      <View style={styles.content}>
        <View style={styles.infoBox}>
          <Ionicons name="shield-checkmark" size={32} color={COLORS.primary} />
          <Text style={styles.infoTitle}>Password Security</Text>
          <Text style={styles.infoText}>
            For your security, we'll send a password reset link to your email address.
          </Text>
        </View>

        <View style={styles.emailBox}>
          <Text style={styles.label}>Reset will be sent to:</Text>
          <View style={styles.emailDisplay}>
            <Ionicons name="mail" size={20} color={COLORS.primary} />
            <Text style={styles.emailText}>{user?.email}</Text>
          </View>
        </View>

        <AppButton
          title="Send Reset Link"
          onPress={handleSendReset}
          loading={loading}
          style={styles.button}
        />

        <TouchableOpacity onPress={() => navigation.goBack()}>
          <Text style={styles.cancelText}>Cancel</Text>
        </TouchableOpacity>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: COLORS.background,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 16,
    paddingVertical: 16,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  title: {
    fontSize: FONT_SIZES.lg,
    fontWeight: '700',
    color: COLORS.text,
  },
  content: {
    padding: 16,
  },
  infoBox: {
    backgroundColor: '#f0f9ff',
    borderRadius: 12,
    padding: 20,
    alignItems: 'center',
    marginBottom: 24,
  },
  infoTitle: {
    fontSize: FONT_SIZES.base,
    fontWeight: '700',
    color: COLORS.text,
    marginTop: 12,
  },
  infoText: {
    fontSize: FONT_SIZES.sm,
    color: COLORS.textMuted,
    marginTop: 8,
    textAlign: 'center',
    lineHeight: 20,
  },
  emailBox: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
    marginBottom: 24,
  },
  label: {
    fontSize: FONT_SIZES.sm,
    fontWeight: '600',
    color: COLORS.textMuted,
    marginBottom: 12,
  },
  emailDisplay: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 12,
    paddingVertical: 10,
    backgroundColor: '#f5f5f5',
    borderRadius: 8,
  },
  emailText: {
    fontSize: FONT_SIZES.base,
    fontWeight: '600',
    color: COLORS.text,
    marginLeft: 12,
    flex: 1,
  },
  button: {
    marginBottom: 12,
  },
  cancelText: {
    fontSize: FONT_SIZES.base,
    fontWeight: '600',
    color: COLORS.textMuted,
    textAlign: 'center',
    paddingVertical: 12,
  },
});

export default ChangePasswordScreen;
