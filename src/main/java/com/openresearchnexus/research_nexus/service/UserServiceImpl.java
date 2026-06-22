    package com.openresearchnexus.research_nexus.service;
    import com.openresearchnexus.research_nexus.dto.LoginRequest;
    import com.openresearchnexus.research_nexus.dto.LoginResponse;
    import com.openresearchnexus.research_nexus.entity.Role;
    import com.openresearchnexus.research_nexus.entity.User;
    import com.openresearchnexus.research_nexus.repository.UserRepository;
    import com.openresearchnexus.research_nexus.util.JwtUtil;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.stereotype.Service;

    import java.util.List;
    import java.util.Optional;

    @Service
    public class UserServiceImpl implements UserService{

        @Autowired
        private  UserRepository userRepository;

        @Autowired
        private BCryptPasswordEncoder encoder;

        @Autowired
        private JwtUtil jwtUtil;



        @Override
        public User createUser(User user) {

            user.setPassword(encoder.encode(user.getPassword()));
            user.setRole(Role.USER);
            return userRepository.save(user);
        }

        @Override
        public Optional<User> getUserByEmail(String email) {
            return userRepository.findByEmail(email);
        }

        @Override
        public List<User> getUserByName(String name) {
            return userRepository.findByName(name);
        }

        @Override
        public User saveUser(User user) {
            return userRepository.save(user);
        }

        @Override
        public LoginResponse login(LoginRequest request) {

            Optional<User> userOpt=userRepository.findByEmail(request.getEmail());

            if(userOpt.isPresent())
            {
                User user=userOpt.get();

               if(encoder.matches(request.getPassword(),user.getPassword()))
               {
                   String token=jwtUtil.generateToken(user.getEmail());

                   LoginResponse response = new LoginResponse(token);
                   return response;
               }

            }
            throw new RuntimeException("Invalid credentials");
        }

    }


