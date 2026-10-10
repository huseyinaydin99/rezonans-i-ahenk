package tr.com.huseyinaydin.application.like.commands;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.ports.ILikeRepository;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;
import java.util.Map;
import java.util.HashMap;

public record ToggleLikeCommand(String userEmail, Long songId) implements ICommand<Map<String, Object>> {
    @Component
    public static class Handler implements ICommandHandler<ToggleLikeCommand, Map<String, Object>> {
        private final ILikeRepository likeRepository;

        public Handler(ILikeRepository likeRepository) {
            this.likeRepository = likeRepository;
        }

        @Override
        public Map<String, Object> handle(ToggleLikeCommand command) {
            boolean currentlyLiked = likeRepository.isLiked(command.userEmail(), command.songId());
            boolean newStatus;
            
            if (currentlyLiked) {
                likeRepository.removeLike(command.userEmail(), command.songId());
                newStatus = false;
            } else {
                likeRepository.addLike(command.userEmail(), command.songId());
                newStatus = true;
            }
            
            Map<String, Object> res = new HashMap<>();
            res.put("liked", newStatus);
            return res;
        }
    }
}
