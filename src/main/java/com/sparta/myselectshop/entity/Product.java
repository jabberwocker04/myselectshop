package com.sparta.myselectshop.entity;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.naver.dto.ItemDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity // JPA가 관리할 수 있는 Entity 클래스 지정
@Getter
@Setter
@Table(name = "product") // 매핑할 테이블의 이름을 지정
@NoArgsConstructor
public class Product extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String link;

    @Column(nullable = false)
    private int lprice;

    @Column(nullable = false)
    private int myprice;

    @ManyToOne(fetch = FetchType.LAZY) // 다대일 단방향, 회원정보가 정말로 필요할 때만 조회할 수 있도록 LAZY타입으로 지정
    @JoinColumn(name = "user_id", nullable = false) // 무조건 회원만 상품을 등록할 수 있으므로 nullable = false(회원만 가능하므로 회원은 null할 수 없음
    private User user;

    @OneToMany(mappedBy = "product") // 양방향 관계므로 주인이 될 외래키를 알려줘야 한다. 타겟팅이 될 엔티티를 알려줘야 하므로 동일한 필드명을 써줘야 한다.
    private List<ProductFolder> productFolderList = new ArrayList<>();

    public Product(ProductRequestDto requestDto, User user) {
        this.title = requestDto.getTitle();
        this.image = requestDto.getImage();
        this.link = requestDto.getLink();
        this.lprice = requestDto.getLprice();
        this.user = user;
    }

    public void update(ProductMypriceRequestDto requestDto) {

        this.myprice = requestDto.getMyprice();

    }

    public void updateByItemDto(ItemDto itemDto){
        this.lprice = itemDto.getLprice();
    }

}