package org.openspcoop2.security.message.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.mvc.properties.provider.IProvider;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.provider.ProviderValidationException;
import org.openspcoop2.security.message.constants.EncryptionAlgorithm;
import org.openspcoop2.security.message.constants.EncryptionC14NAlgorithm;
import org.openspcoop2.security.message.constants.EncryptionDigestAlgorithm;
import org.openspcoop2.security.message.constants.EncryptionKeyTransportAlgorithm;
import org.openspcoop2.security.message.constants.EncryptionSymmetricKeyWrapAlgorithm;
import org.openspcoop2.security.message.constants.KeyAlgorithm;
import org.openspcoop2.security.message.constants.SignatureAlgorithm;
import org.openspcoop2.security.message.constants.SignatureC14NAlgorithm;
import org.openspcoop2.security.message.constants.SignatureDigestAlgorithm;
public class SecurityProvider implements IProvider {

	@Override
	public void validate(Map<String, Properties> mapProperties) throws ProviderException, ProviderValidationException {

	}

	@Override
	public List<String> getValues(String id) throws ProviderException {
		if(XMLCostanti.ID_SIGNATURE_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			SignatureAlgorithm [] tmp = SignatureAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].getUri());
			}
			return l;
		}
		else if(XMLCostanti.ID_SIGNATURE_DIGEST_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			SignatureDigestAlgorithm [] tmp = SignatureDigestAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].getUri());
			}
			return l;
		}
		else if(XMLCostanti.ID_SIGNATURE_C14N_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			SignatureC14NAlgorithm [] tmp = SignatureC14NAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].getUri());
			}
			return l;
		}
		
		else if(XMLCostanti.ID_ENCRYPT_KEY_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			KeyAlgorithm [] tmp = KeyAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].name());
			}
			return l;
		}
		else if(XMLCostanti.ID_ENCRYPT_SYMMETRIC_KEY_WRAP_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			EncryptionSymmetricKeyWrapAlgorithm [] tmp = EncryptionSymmetricKeyWrapAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].getUri());
			}
			return l;
		}
		else if(XMLCostanti.ID_ENCRYPT_TRANSPORT_KEY_WRAP_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			EncryptionKeyTransportAlgorithm [] tmp = EncryptionKeyTransportAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].getUri());
			}
			return l;
		}
		else if(XMLCostanti.ID_ENCRYPT_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			EncryptionAlgorithm [] tmp = EncryptionAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].getUri());
			}
			return l;
		}
		else if(XMLCostanti.ID_ENCRYPT_DIGEST_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			EncryptionDigestAlgorithm [] tmp = EncryptionDigestAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].getUri());
			}
			return l;
		}
		else if(XMLCostanti.ID_ENCRYPT_C14N_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			EncryptionC14NAlgorithm [] tmp = EncryptionC14NAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].getUri());
			}
			return l;
		}
		
		return null;
	}

	@Override
	public List<String> getLabels(String id) throws ProviderException {
		if(XMLCostanti.ID_SIGNATURE_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			SignatureAlgorithm [] tmp = SignatureAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(this.convertEnumName(tmp[i].name()));
			}
			return l;
		}
		else if(XMLCostanti.ID_SIGNATURE_DIGEST_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			SignatureDigestAlgorithm [] tmp = SignatureDigestAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(this.convertEnumName(tmp[i].name()));
			}
			return l;
		}
		else if(XMLCostanti.ID_SIGNATURE_C14N_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			SignatureC14NAlgorithm [] tmp = SignatureC14NAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(tmp[i].getLabel());
			}
			return l;
		}
		
		else if(XMLCostanti.ID_ENCRYPT_KEY_ALGORITHM.equals(id)) {
			return this.getValues(id);
		}
		else if(XMLCostanti.ID_ENCRYPT_SYMMETRIC_KEY_WRAP_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			EncryptionSymmetricKeyWrapAlgorithm [] tmp = EncryptionSymmetricKeyWrapAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(this.convertEnumName(tmp[i].name()));
			}
			return l;
		}
		else if(XMLCostanti.ID_ENCRYPT_TRANSPORT_KEY_WRAP_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			EncryptionKeyTransportAlgorithm [] tmp = EncryptionKeyTransportAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(this.convertEnumName(tmp[i].name()));
			}
			return l;
		}
		else if(XMLCostanti.ID_ENCRYPT_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			EncryptionAlgorithm [] tmp = EncryptionAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(this.convertEnumName(tmp[i].name()));
			}
			return l;
		}
		else if(XMLCostanti.ID_ENCRYPT_DIGEST_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			EncryptionDigestAlgorithm [] tmp = EncryptionDigestAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(this.convertEnumName(tmp[i].name()));
			}
			return l;
		}
		else if(XMLCostanti.ID_ENCRYPT_C14N_ALGORITHM.equals(id)) {
			List<String> l = new ArrayList<>();
			EncryptionC14NAlgorithm [] tmp = EncryptionC14NAlgorithm.values();
			for (int i = 0; i < tmp.length; i++) {
				l.add(this.convertEnumName(tmp[i].name()));
			}
			return l;
		}

		return this.getValues(id);
	}
	
	private String convertEnumName(String name) {
		if(name.contains("_")) {
			String t = new String(name);
			while(t.contains("_")) {
				t = t.replace("_", "-");
			}
			return t;
		}
		else {
			return name;
		}
	}

	@Override
	public String getDefault(String id) throws ProviderException {
		if(XMLCostanti.ID_SIGNATURE_ALGORITHM.equals(id)) {
			return SignatureAlgorithm.RSA_SHA256.getUri();
		}
		else if(XMLCostanti.ID_SIGNATURE_DIGEST_ALGORITHM.equals(id)) {
			return SignatureDigestAlgorithm.SHA256.getUri();
		}
		else if(XMLCostanti.ID_SIGNATURE_C14N_ALGORITHM.equals(id)) {
			return SignatureC14NAlgorithm.INCLUSIVE_C14N_10_WITH_COMMENTS.getUri();
		}
		
		else if(XMLCostanti.ID_ENCRYPT_KEY_ALGORITHM.equals(id)) {
			return KeyAlgorithm.AES.name();
		}
		else if(XMLCostanti.ID_ENCRYPT_SYMMETRIC_KEY_WRAP_ALGORITHM.equals(id)) {
			return EncryptionSymmetricKeyWrapAlgorithm.AES_256.getUri();
		}
		else if(XMLCostanti.ID_ENCRYPT_ALGORITHM.equals(id)) {
			return EncryptionAlgorithm.AES_256.getUri();
		}
		else if(XMLCostanti.ID_ENCRYPT_TRANSPORT_KEY_WRAP_ALGORITHM.equals(id)) {
			return EncryptionKeyTransportAlgorithm.RSA_v1dot5.getUri();
		}
		else if(XMLCostanti.ID_ENCRYPT_DIGEST_ALGORITHM.equals(id)) {
			return EncryptionDigestAlgorithm.SHA256.getUri();
		}
		else if(XMLCostanti.ID_ENCRYPT_C14N_ALGORITHM.equals(id)) {
			return EncryptionC14NAlgorithm.INCLUSIVE_C14N_10_WITH_COMMENTS.getUri();
		}
		
		return null;
	}

}
