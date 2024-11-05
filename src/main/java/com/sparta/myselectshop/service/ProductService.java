package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.*;
import com.sparta.myselectshop.naver.dto.ItemDto;
import com.sparta.myselectshop.repository.FolderRepository;
import com.sparta.myselectshop.repository.ProductFolderRepository;
import com.sparta.myselectshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final FolderRepository folderRepository;
    private final ProductFolderRepository productFolderRepository;


    public static final int MIN_MY_PRICE = 100;

    public ProductResponseDto createProduct(ProductRequestDto requestDto, User user) {

        Product product = productRepository.save(new Product(requestDto, user));
        return new ProductResponseDto(product);

    }

    @Transactional//update이므로 transacitonal 넣어주어야 함.
    public ProductResponseDto updateProduct(Long id, ProductMypriceRequestDto requestDto) {
        int myprice = requestDto.getMyprice();
        if (myprice < MIN_MY_PRICE) {
            throw new IllegalArgumentException("유효하지 않은 관심 가격입니다. 최소 " + MIN_MY_PRICE + "원 이상으로 설정해 주세요.");
        }

        Product product = productRepository.findById(id).orElseThrow(() ->
                new NullPointerException("해당 상품을 찾을 수 없습니다.")
        );

        product.update(requestDto);

        return new ProductResponseDto(product);
    }

    @Transactional(readOnly = true) //product에서의 @ManyToOne의 지연로딩기능을 이용하기 위함
    public Page<ProductResponseDto> getProducts(User user, int page, int size, String sortBy, Boolean isAsc) {
        //페이징 처리를 위해 page, size, sortBy, isAsc가 추가 됌.
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort); // pageable 객체 만들어줌

        UserRoleEnum userRoleEnum = user.getRole();

        Page<Product> productList;

        if(userRoleEnum == UserRoleEnum.USER) {
            productList = productRepository.findAllByUser(user, pageable);
        } else {
            productList = productRepository.findAll(pageable);
        }

//        List<Product> productList = productRepository.findAllByUser(user); // var 명령어로 생성
//
//        List<ProductResponseDto> responseDtoList = new ArrayList<>();
//
//        for (Product product : productList) { // iter(향상된 for문) 명령어로 생성
//            responseDtoList.add(new ProductResponseDto(product));
//        }

        return productList.map(ProductResponseDto::new); // map 메서드를 사용하여 Page타입에 들어있는 Product를 하나씩 돌리면서 ProductResponseDto가 생성된다.


    }

    public void updateBySearch(Long id, ItemDto itemDto) {
        Product product = productRepository.findById(id).orElseThrow(()->
                new NullPointerException("해당 상품은 존재하지 않습니다.")
        );

        product.updateByItemDto(itemDto);
    }

    public void addFolder(Long productId, Long folderId, User user) {

        Product product = productRepository.findById(productId).orElseThrow(
                ()-> new NullPointerException("해당 상품이 존재하지 않습니다.")
        );

        Folder folder = folderRepository.findById(folderId).orElseThrow(
                () -> new NullPointerException("해당 폴더가 존재하지 않습니다.")
        );

        if(!product.getUser().getId().equals(user.getId())
        || !folder.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("회원님의 관심 상품이 아니거나, 회원님의 폴더가 아닙니다.");
        }//같지 않다면 false가 나오게 된다. !와 만나서 true가 된다.

        Optional<ProductFolder> overlapFolder = productFolderRepository.findByProductAndFolder(product, folder);

        if(overlapFolder.isPresent()) {
            throw new IllegalArgumentException("중복된 폴더입니다._service");
        }

        productFolderRepository.save(new ProductFolder(product, folder)); // 앤티티 객체 하나가 데이터베이스에서는 한 줄이 된다.

    }

    public Page<ProductResponseDto> getProductsInFolder(Long folderId, int page, int size, String sortBy, Boolean isAsc, User user) {

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort); // pageable 객체 만들어줌 || 페이징 처리 중복되서 위의 것 복붙함

        Page<Product> productsList = productRepository.findAllByUserAndProductFolderList_FolderId(user, folderId, pageable);

        Page<ProductResponseDto> responseDtoList = productsList.map(ProductResponseDto::new);

        return responseDtoList;

    }

//    public List<ProductResponseDto> getAllProducts() { // 유저가 아니라 관리자로 들어온다면 실행될 수 있도록 그냥 getAllProducts를 해줌
//        List<Product> productList = productRepository.findAll(); // var 명령어로 생성
//
//        List<ProductResponseDto> responseDtoList = new ArrayList<>();
//
//        for (Product product : productList) { // iter(향상된 for문) 명령어로 생성
//            responseDtoList.add(new ProductResponseDto(product));
//        }
//
//        return responseDtoList;
//
//    } // Page객체를 여기서도 만들어줘야하기 때문에 이것을 또 Pageable 객체를 만드는 것은 비효율적이므로 Pageable이 포함된 메서드에서 User인지 ADmin인지 구분
}
