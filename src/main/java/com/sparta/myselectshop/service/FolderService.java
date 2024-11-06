package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.FolderRequestDto;
import com.sparta.myselectshop.dto.FolderResponseDto;
import com.sparta.myselectshop.entity.Folder;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;

    public void addFolders(List<String> folderNames, User user) {

        List<Folder> existFolderList = folderRepository.findAllByUserAndNameIn(user, folderNames);// FindALl로 전체를 찾을건데 User을 조건으로 Name여러개를 찾으려면 한다 In을 붙여준다

        List<Folder> folderList = new ArrayList<>();

        for (String folderName : folderNames) { // iter 사용해 줌
            if (!isExistFolderName(folderName, existFolderList)) { // 존재하지 않으면 생성될 수 있도록 !을 붙여주어야 했는데 !을 붙여주지 못해서 계속해서 중복 에러가 발생하던 것이었다.
                Folder folder = new Folder(folderName, user); // 새로운 폴더를 만들어주기 위한 folderName, user를 넣어주지 않아 계속해서 폴더명 중복이 발생했다.
                folderList.add(folder);
            } else {
                throw new IllegalArgumentException("중복된 폴더명을 제거해주세요! 폴더명: " + folderName);
            }
        }

        folderRepository.saveAll(folderList);
    }


    public List<FolderResponseDto> getFolders(User user) {

        List<Folder> folderList = folderRepository.findAllByUser(user);

        List<FolderResponseDto> responseDtoList = new ArrayList<>();

        for (Folder folder : folderList) {
            responseDtoList.add(new FolderResponseDto(folder)); // 생성자에 의해서 FOlder를 받아와서 folderResponseDto에 넣어주고 그렇게 생긴 responseDtoList
        }

        return responseDtoList;
    }

    private boolean isExistFolderName(String folderName, List<Folder> existFolderList) {
        //폴더 이름 중복 확인 메서드
        for (Folder existFolder : existFolderList) {
            if(folderName.equals(existFolder.getName())) {
                return true;
            }
        }
        return false;
    }

}
