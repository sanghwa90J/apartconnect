package com.aptconnect.menu.invite.repository;

import com.aptconnect.menu.invite.entity.InviteCodeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteCodeRequestRepository extends JpaRepository<InviteCodeRequest,Long> {

}
