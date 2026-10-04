package tr.com.huseyinaydin.application.ports;

import tr.com.huseyinaydin.application.auth.dtos.RegisterRequestDto;

public interface IRegisterService {
    void registerNewUser(RegisterRequestDto request);
}
