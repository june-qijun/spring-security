/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security.crypto.encrypt;

import org.junit.Before;
import org.junit.Test;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.crypto.encrypt.AesBytesEncryptor.CipherAlgorithm.GCM;

/**
 * Tests for {@link AesBytesEncryptor}
 */
public class AesBytesEncryptorTests {
	private String secret = "value";
	private String password = "password";
	private String hexSalt = "deadbeef";

	private BytesKeyGenerator generator;

	@Before
	public void setUp() {
		this.generator = mock(BytesKeyGenerator.class);
		when(this.generator.generateKey()).thenReturn(Hex.decode("4b0febebd439db7ca77153cb254520c3"));
		when(this.generator.getKeyLength()).thenReturn(16);
	}

	@Test
	public void roundtripWhenUsingDefaultsThenEncryptsAndDecrypts() {
		AesBytesEncryptor encryptor = new AesBytesEncryptor(this.password, this.hexSalt);
		byte[] encryption = encryptor.encrypt(this.secret.getBytes());
		byte[] decryption = encryptor.decrypt(encryption);
		assertThat(new String(decryption)).isEqualTo(this.secret);
	}

	@Test
	public void roundtripWhenUsingDefaultCipherThenEncryptsAndDecrypts() {
		AesBytesEncryptor encryptor = new AesBytesEncryptor(this.password, this.hexSalt, this.generator);
		byte[] encryption = encryptor.encrypt(this.secret.getBytes());
		assertThat(new String(Hex.encode(encryption)))
				.isEqualTo("4b0febebd439db7ca77153cb254520c3b7232ac29355d07869433f1ecf55fe94");

		byte[] decryption = encryptor.decrypt(encryption);
		assertThat(new String(decryption)).isEqualTo(this.secret);
	}

	@Test
	public void roundtripWhenUsingGcmThenEncryptsAndDecrypts() {
		AesBytesEncryptor encryptor = new AesBytesEncryptor(this.password, this.hexSalt, this.generator, GCM);
		byte[] encryption = encryptor.encrypt(this.secret.getBytes());
		assertThat(new String(Hex.encode(encryption)))
				.isEqualTo("4b0febebd439db7ca77153cb254520c3e4d61ae38207b4e42b820d311dc3d4e0e2f37ed5ee");

		byte[] decryption = encryptor.decrypt(encryption);
		assertThat(new String(decryption)).isEqualTo(this.secret);
	}

}