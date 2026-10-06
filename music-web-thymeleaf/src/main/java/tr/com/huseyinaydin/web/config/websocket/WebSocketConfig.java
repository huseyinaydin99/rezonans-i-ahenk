package tr.com.huseyinaydin.web.config.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // İstemcilere (Tarayıcıya) mesaj gönderilecek hedef kök dizin
        config.enableSimpleBroker("/topic", "/queue");
        // İstemcilerden sunucuya gelecek mesajların kök dizini
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // İstemcilerin WebSocket bağlantısını başlatacağı uç nokta
        // SockJS (Geriye dönük tarayıcı uyumluluğu) ile desteklenir
        registry.addEndpoint("/ws-music").withSockJS();
    }
}
