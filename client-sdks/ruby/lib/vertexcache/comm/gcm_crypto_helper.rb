# ------------------------------------------------------------------------------
# Copyright 2025 to Present, Jason Lam - VertexCache (https://github.com/vertexcache/vertexcache)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ------------------------------------------------------------------------------

require 'openssl'
require 'base64'

module VertexCache
  module Comm
    class GcmCryptoHelper
      IV_LENGTH = 12
      TAG_LENGTH = 16
      KEY_LENGTH = 32

      def self.encrypt(data, key)
        raise ArgumentError, 'Invalid key length' unless key.bytesize == 16 || key.bytesize == 32

        iv = OpenSSL::Random.random_bytes(IV_LENGTH)
        cipher = OpenSSL::Cipher.new("aes-#{key.bytesize * 8}-gcm")
        cipher.encrypt
        cipher.key = key
        cipher.iv = iv

        ciphertext = cipher.update(data) + cipher.final
        tag = cipher.auth_tag(TAG_LENGTH)

        iv + ciphertext + tag
      end

      def self.decrypt(encrypted, key)
        raise ArgumentError, 'Encrypted data too short' if encrypted.bytesize < IV_LENGTH + TAG_LENGTH

        iv = encrypted[0, IV_LENGTH]
        tag = encrypted[-TAG_LENGTH, TAG_LENGTH]
        ciphertext = encrypted[IV_LENGTH..-(TAG_LENGTH + 1)]

        cipher = OpenSSL::Cipher.new("aes-#{key.bytesize * 8}-gcm")
        cipher.decrypt
        cipher.key = key
        cipher.iv = iv
        cipher.auth_tag = tag

        cipher.update(ciphertext) + cipher.final
      end

      def self.encode_base64_key(key)
        Base64.strict_encode64(key)
      end

      def self.decode_base64_key(encoded)
        Base64.strict_decode64(encoded)
      end

      def self.generate_base64_key
        Base64.strict_encode64(OpenSSL::Random.random_bytes(KEY_LENGTH))
      end

      def self.encrypt_with_iv(data, key, iv)
        cipher = OpenSSL::Cipher.new("aes-#{key.bytesize * 8}-gcm")
        cipher.encrypt
        cipher.key = key
        cipher.iv = iv

        ciphertext = cipher.update(data) + cipher.final
        tag = cipher.auth_tag(TAG_LENGTH)

        iv + ciphertext + tag
      end

      def self.decrypt_with_iv(encrypted, key, iv)
        tag = encrypted[-TAG_LENGTH, TAG_LENGTH]
        ciphertext = encrypted[IV_LENGTH..-(TAG_LENGTH + 1)]

        cipher = OpenSSL::Cipher.new("aes-#{key.bytesize * 8}-gcm")
        cipher.decrypt
        cipher.key = key
        cipher.iv = iv
        cipher.auth_tag = tag

        cipher.update(ciphertext) + cipher.final
      end
    end
  end
end
