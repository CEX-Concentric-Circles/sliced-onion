package concentric.circles.sliced_onion.unit.product

import concentric.circles.sliced_onion.product.internal.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.util.UriComponentsBuilder
import java.util.*

@ExtendWith(MockitoExtension::class)
class ProductControllerTest {

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var productService: ProductService

    @InjectMocks
    private lateinit var productController: ProductController

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build()
    }

    @Test
    fun `should return all products`() {
        val products = listOf(
            Product(UUID.randomUUID(), "Product 1", Price(100.0)),
            Product(UUID.randomUUID(), "Product 2", Price(150.0))
        )
        val productDtos = products.map { ProductDto(it) }

        `when`(productService.getProducts()).thenReturn(products)

        mockMvc.perform(get("/product"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].productId").value(products[0].productId.toString()))
            .andExpect(jsonPath("$[1].name").value(products[1].name))
    }

    @Test
    fun `should return product by id`() {
        val productId = UUID.randomUUID()
        val product = Product(productId, "Product Name", Price(100.0))
        `when`(productService.getProduct(any(UUID::class.java))).thenReturn(product)

        mockMvc.perform(get("/product/$productId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.productId").value(productId.toString()))
            .andExpect(jsonPath("$.name").value(product.name))
            .andExpect(jsonPath("$.price.amount").value(product.price.amount))
            .andExpect(jsonPath("$.price.currency").value(product.price.currency.toString()))
    }

    @Test
    fun `should return 404 when product not found`() {
        val productId = UUID.randomUUID()
        `when`(productService.getProduct(productId)).thenReturn(null)

        mockMvc.perform(get("/product/$productId"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should create product with valid data`() {
        val productDto = ProductDto(UUID.randomUUID(), "New Product", Price(200.0))
        val product = Product(productDto)
        val uriComponentsBuilder = UriComponentsBuilder.fromUriString("/product/${product.productId}")

        `when`(productService.createProduct(any(ProductDto::class.java))).thenReturn(product)

        mockMvc.perform(
            post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"productId":"${productDto.productId}","name":"${productDto.name}","price":{"amount":${productDto.price.amount},"currency":"${productDto.price.currency}"}}""")
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "http://localhost/product/${product.productId}"))
            .andExpect(jsonPath("$.productId").value(product.productId.toString()))
            .andExpect(jsonPath("$.name").value(productDto.name))
            .andExpect(jsonPath("$.price.amount").value(productDto.price.amount))
    }

    @Test
    fun `should return 400 when creating product with invalid data`() {
        val invalidProductDto = """{"name":"","price":{"amount":-100.0,"currency":"USD"}}"""

        mockMvc.perform(
            post("/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidProductDto)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should update product with valid data`() {
        val productId = UUID.randomUUID()
        val productDto = ProductDto(productId, "Updated Name", Price(150.0))
        val updatedProduct = Product(productDto)

        `when`(productService.updateProduct(any(UUID::class.java), any(productDto::class.java))).thenReturn(updatedProduct)

        mockMvc.perform(
            put("/product/$productId")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"${productDto.name}","price":{"amount":${productDto.price.amount},"currency":"${productDto.price.currency}"}}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.productId").value(updatedProduct.productId.toString()))
            .andExpect(jsonPath("$.name").value(productDto.name))
            .andExpect(jsonPath("$.price.amount").value(productDto.price.amount))
    }

    @Test
    fun `should return 400 when updating product fails`() {
        val productId = UUID.randomUUID()
        val productDto = ProductDto(productId, "Updated Name", Price(150.0))

        `when`(productService.updateProduct(any(UUID::class.java), any(ProductDto::class.java))).thenReturn(null)

        mockMvc.perform(
            put("/product/$productId")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"${productDto.name}","price":{"amount":${productDto.price.amount},"currency":"${productDto.price.currency}"}}""")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should delete product`() {
        val productId = UUID.randomUUID()
        val product = Product(productId, "Product Name", Price(100.0))

        `when`(productService.getProduct(productId)).thenReturn(product)

        mockMvc.perform(delete("/product/$productId"))
            .andExpect(status().isNoContent)

        verify(productService).deleteProduct(product)
    }

    @Test
    fun `should return 404 when deleting non-existent product`() {
        val productId = UUID.randomUUID()

        `when`(productService.getProduct(productId)).thenReturn(null)

        mockMvc.perform(delete("/product/$productId"))
            .andExpect(status().isNotFound)
    }
}
