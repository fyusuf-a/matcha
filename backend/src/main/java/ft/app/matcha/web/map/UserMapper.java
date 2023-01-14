package ft.app.matcha.web.map;

import ft.app.matcha.domain.relationship.RelationshipService;
import ft.app.matcha.domain.user.User;
import ft.app.matcha.web.dto.RelationshipDto;
import ft.app.matcha.web.dto.UserDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserMapper {
	
	private final RelationshipService relationshipService;
	
	public UserDto toDto(User user) {
		return new UserDto()
			.setId(user.getId())
			.setLogin(user.getLogin())
			.setFirstName(user.getFirstName())
			.setLastName(user.getLastName())
			.setBiography(user.getBiography())
			.setGender(user.getGender())
			.setSexualOrientation(user.getSexualOrientation())
			.setFame(user.getFame())
			.setEmailConfirmed(user.isEmailConfirmed());
	}
	
	public UserDto toDto(User user, User principal) {
		if (principal == null) {
			return toDto(user);
		} else {
			return toDtoAuthenticated(user, principal);
		}
	}
	
	private UserDto toDtoAuthenticated(User user, User principal) {
		final var relationship = getRelationship(principal, user);
		
		return toDto(user)
			.setRelationship(relationship);
	}
	
	public RelationshipDto getRelationship(User user, User peer) {
		if (user.getId() == peer.getId()) {
			return null;
		}
		
		final var relationship = relationshipService.find(user, peer).orElse(null);
		if (relationship == null) {
			return null;
		}
		
		final var mutual = relationshipService.isMutual(relationship);
		return new RelationshipDto(relationship.getType(), mutual, relationship.getCreatedAt());
	}
	
}