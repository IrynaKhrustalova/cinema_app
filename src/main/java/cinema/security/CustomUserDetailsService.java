package cinema.security;

import cinema.model.User;
import cinema.service.UserService;
import java.util.Optional;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Optional<User> userFromDb = userService.findByEmail(email);
        UserBuilder userBuilder;
        if (userFromDb.isPresent()) {
            userBuilder = org.springframework.security.core.userdetails.User
                    .withUsername(email);
            userBuilder.password(userFromDb.get().getPassword());
            userBuilder.authorities(userFromDb.get().getRoles()
                    .stream()
                    .map(e -> e.getRoleName().name())
                    .toArray(String[]::new));
            return userBuilder.build();
        }
        throw new UsernameNotFoundException("User not found.");
    }
}
