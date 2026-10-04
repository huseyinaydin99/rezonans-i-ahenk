package tr.com.huseyinaydin.application.ports;

import tr.com.huseyinaydin.application.auth.dtos.LoginRequestDto;
import tr.com.huseyinaydin.application.auth.dtos.ResponseTokenDto;

public interface ILoginService {
    ResponseTokenDto authenticate(LoginRequestDto request);
}
