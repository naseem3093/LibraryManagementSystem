package com.example.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.exceptions.MemberNotFoundException;
import com.example.model.Member;
import com.example.repository.MemberRepository;

@Service
public class MemberServiceImpl implements MemberService {
	private static final Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

	@Autowired
	private MemberRepository repository;

	@Override
	public String registerMember(Member member) {
		logger.info("Registering new member: {}", member);

		// Set initial membership expire date (1 year from now)
		member.setMembershipExpiryDate(LocalDate.now().plusYears(1));
		repository.save(member);

		logger.info("Member registered successfully: ID {}", member.getMemberId());
		return "Member registered successfully!";
	}

	@Override
	public Member updateMember(Member member) {
		logger.info("Updating member details: ID {}", member.getMemberId());
		Member updatedMember = repository.save(member);
		logger.info("Member updated successfully: ID {}", updatedMember.getMemberId());
		return updatedMember;
	}

	@Override
	public Member getMember(int memberId) throws MemberNotFoundException {
		logger.info("Fetching member details: ID {}", memberId);

		Optional<Member> optional = repository.findById(memberId);
		if (optional.isPresent()) {
			logger.info("Member found: ID {}", memberId);
			return optional.get();
		} else {
			logger.error("Member with ID {} not found", memberId);
			throw new MemberNotFoundException("Member ID is not valid");
		}
	}

	@Override
	public List<Member> getAllMembers() {
		logger.info("Fetching all members...");
		List<Member> members = repository.findAll();
		logger.info("Total members found: {}", members.size());
		return members;
	}

	@Override
	public String changeMembershipStatus(int memberId, String status) throws MemberNotFoundException {
		logger.info("Changing membership status for Member ID: {} to {}", memberId, status);
		Member member = getMember(memberId);
		member.setMembershipStatus(status);
		repository.save(member);
		logger.info("Membership status updated successfully for Member ID: {}", memberId);
		return "Membership status updated successfully!";
	}

	@Override
	public String renewMembership(int memberId) throws MemberNotFoundException {
		logger.info("Renewing membership for Member ID: {}", memberId);

		Member member = getMember(memberId);
		// Extend membership for another year
		member.setMembershipExpiryDate(LocalDate.now().plusYears(1));
		repository.save(member);

		logger.info("Membership renewed successfully for Member ID: {}", memberId);
		return "Membership renewed successfully!";
	}

	@Override
	public String deleteMember(int memberId) throws MemberNotFoundException {
		logger.info("Attempting to delete member: ID {}", memberId);

		if (!repository.existsById(memberId)) {
			logger.error("Member with ID {} not found", memberId);
			throw new MemberNotFoundException("Member with ID " + memberId + " not found!");
		}

		repository.deleteById(memberId);
		logger.info("Member deleted successfully: ID {}", memberId);
		return "Member deleted successfully!";
	}
}
