package com.commerce.service.product.application.service

import com.commerce.common.model.category.CategoryRepository
import com.commerce.common.model.product.ProductRepository
import com.commerce.common.model.product.SaleStatus
import com.mock.FakeCategoryRepository
import com.mock.FakeProductRepository
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProductServiceTest {

    private lateinit var productRepository: ProductRepository
    private lateinit var categoryRepository: CategoryRepository
    private val productService by lazy {
        ProductService(productRepository, categoryRepository)
    }

    @BeforeEach
    fun setUp() {
        productRepository = FakeProductRepository()
        categoryRepository = FakeCategoryRepository()
    }

    @Test
    fun `카테고리 전체 리스트를 출력한다`() {
        // given
        // when
        val categories = categoryRepository.findAll()
        // then
        assertThat(categories).hasSize(2)
        assertThat(categories[0].id).isEqualTo(1L)
        assertThat(categories[0].parentId).isEqualTo(0L)
        assertThat(categories[0].name).isEqualTo("국내도서")
        assertThat(categories[0].depth).isEqualTo(1)

        assertThat(categories[1].id).isEqualTo(2L)
        assertThat(categories[1].parentId).isEqualTo(0L)
        assertThat(categories[1].name).isEqualTo("해외도서")
        assertThat(categories[1].depth).isEqualTo(1)
    }

    @Test
    fun `카테고리 id로 product를 검색한다`() {
        // when
        val products = productRepository.findByCategoryId(2L, 0, 20)

        // then
        assertThat(products).hasSize(1)
        assertThat(products[0].title).isEqualTo("제목")
        assertThat(products[0].author).isEqualTo("작가")
        assertThat(products[0].price).isEqualTo(BigDecimal("10000"))
        assertThat(products[0].discountedPrice).isEqualTo(BigDecimal("1000"))
        assertThat(products[0].publisher).isEqualTo("퍼블리셔")
        assertThat(products[0].isbn).isEqualTo("1111")
        assertThat(products[0].status).isEqualTo(SaleStatus.ON_SALE)
        assertThat(products[0].category?.id).isEqualTo(2L)
        assertThat(products[0].category?.name).isEqualTo("국내도서")
    }
}