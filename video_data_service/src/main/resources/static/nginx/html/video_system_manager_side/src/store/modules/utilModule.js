import CryptoJS from 'crypto-js';
export default {
    state:{
        key:"",
        decrypt_msg:"",
        encrypt_msg:""
    },
    mutations:{
        decrypt(state,payload){
            let key=payload.key;
            let en_msg=payload.encrypt_msg;
            try {
                // 1. 验证密钥长度
                const keyBuffer = CryptoJS.enc.Utf8.parse(key);
                if (keyBuffer.sigBytes !== 16) {
                    throw new Error('密钥长度必须为16字节（128位）。');
                }
                // 2. 将Base64密文转换为CryptoJS能够识别的格式
                // CryptoJS期望一个包含密文的CipherParams对象，或者可以直接传递Base64字符串
                const decrypted = CryptoJS.AES.decrypt(en_msg, keyBuffer, {
                    mode: CryptoJS.mode.ECB,
                    padding: CryptoJS.pad.Pkcs7 // 确保与加密端填充方案一致，通常为PKCS7
                });
                // 3. 将解密结果转换为UTF-8字符串
                const decryptedText = decrypted.toString(CryptoJS.enc.Utf8);
                if (!decryptedText) {
                    throw new Error('解密结果为空，请检查密钥或密文是否正确。');
                }
                return decryptedText;

            } catch (error) {
                console.error('解密失败：', error);
                // 可以根据错误类型抛出更具体的错误信息
                throw new Error(`解密过程出错: ${error.message}`);
            }
        }
    }
}
