package com.sparta.myselectshop.repository;

import com.sparta.myselectshop.entity.Folder;
import com.sparta.myselectshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findAllByUserAndNameIn(User user, List<String> folderNames);
    //노션 자료에는 없는 내용이다. 이 메서드를 예측한 쿼리문은
    //select * from Folder where user_id = 1 and name in ('1', '2', '3') // user_id == User객체 || and name in == NameIn

    List<Folder> findAllByUser(User user);

}
