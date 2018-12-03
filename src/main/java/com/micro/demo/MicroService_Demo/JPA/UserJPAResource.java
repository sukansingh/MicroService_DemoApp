package com.micro.demo.MicroService_Demo.JPA;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.micro.demo.MicroService_Demo.bean.Post;
import com.micro.demo.MicroService_Demo.bean.User;
import com.micro.demo.MicroService_Demo.customexception.UserNotFoundException;
import com.micro.demo.MicroService_Demo.dao.UserDAOService;

@RestController
public class UserJPAResource {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PostRepository postRepository;
	
	@GetMapping(path="/jpa/users")
	public List<User> retrieveAllUsers(){
		return userRepository.findAll();
	}
	
	@GetMapping(path="/jpa//users/{id}")
	public Resource<User> retrieveUser(@PathVariable int id){
		Optional<User> user =  userRepository.findById(id);
		if(!user.isPresent())
			throw new UserNotFoundException("id-"+id);
		
		//HATEOAS - HyperMedia As The Engine Of Application State
		Resource<User> resource = new Resource<User>(user.get());
		Link link = linkTo(methodOn(this.getClass()).retrieveAllUsers()).withRel("all-users");
		resource.add(link);
		
		
		return resource; //user
	}
	
	@PostMapping(path="/jpa/user")
	public ResponseEntity<User> createUser(@Valid @RequestBody User user){
		userRepository.save(user);
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getId())
				.toUri();
		
		return ResponseEntity.created(location).build();
	}
	
	@DeleteMapping(path="/jpa//user/{id}")
	public ResponseEntity<Object> deleteById(@PathVariable int id){
		
		userRepository.deleteById(id);
			
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping(path="/jpa/users/{id}/posts")
	public List<Post> retrieveAllPosts(@PathVariable int id) {
		Optional<User> userOptional = userRepository.findById(id);
		if(!userOptional.isPresent())
			throw new UserNotFoundException("id-"+id);
		
		return userOptional.get().getPosts();
	}
	
	@PostMapping(path="/jpa/users/{id}/posts")
	public ResponseEntity<User> createPost(@PathVariable int id, @Valid @RequestBody Post post){
		Optional<User> userOptional = userRepository.findById(id);
		if(!userOptional.isPresent())
			throw new UserNotFoundException("id-"+id);
		
		User user = userOptional.get();
		post.setUser(user);
		postRepository.save(post);
		
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(post.getId())
				.toUri();
		
		return ResponseEntity.created(location).build();
	}
	
}
