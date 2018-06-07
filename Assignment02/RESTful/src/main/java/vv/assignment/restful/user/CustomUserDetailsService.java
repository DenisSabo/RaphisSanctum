package vv.assignment.restful.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Class that provides the possibility for authentication through database
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        if (!user.isPresent()) {
            throw new UsernameNotFoundException("User not found");
        }
        List<GrantedAuthority> auth = AuthorityUtils.commaSeparatedStringToAuthorityList(user.get().getRole());

        String password = user.get().getPassword();

        //WebSecurityConfigurerAdapter.setUserId(user.getId());

        return new org.springframework.security.core.userdetails.User(username, password, auth);
    }

    public void saveUser(User user) throws org.springframework.dao.DataIntegrityViolationException{
        userRepository.save(user);
    }

    public List<User> getAllUsers(){ return userRepository.findAll(); }
}
