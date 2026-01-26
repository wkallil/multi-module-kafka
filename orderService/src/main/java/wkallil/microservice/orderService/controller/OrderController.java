package wkallil.microservice.orderService.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wkallil.microservice.orderService.controller.docs.OrderControllerDocs;
import wkallil.microservice.orderService.dto.requestDto.CreateOrderRequestDto;
import wkallil.microservice.orderService.dto.responseDto.OrderResponseDto;
import wkallil.microservice.orderService.model.OrderStatus;
import wkallil.microservice.orderService.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "APIs for managing customer orders")
public class OrderController implements OrderControllerDocs {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(
            summary = "Create a new order",
            description = "Creates a new order and publishes event to Kafka for inventory verification."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Order created successfully",
                            content = @Content(schema = @Schema(implementation = OrderResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data"
                    )
            }
    )
    @PostMapping
    @Override
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody CreateOrderRequestDto request) {
        OrderResponseDto responseDto = orderService.createOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(
            summary = "Get order by ID",
            description = "Retrieves a specific order by its unique identifier."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Order found",
                            content = @Content(schema = @Schema(implementation = OrderResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Order not found"
                    )
            }
    )
    @GetMapping("/{id}")
    @Override
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        OrderResponseDto responseDto = orderService.getOrderById(id);

        return ResponseEntity.ok(responseDto);
    }

    @Operation(
            summary = "Get order by order number",
            description = "Retrieves a specific order by its order number."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Order found",
                            content = @Content(schema = @Schema(implementation = OrderResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Order not found"
                    )
            }
    )
    @GetMapping("/number/{orderNumber}")
    @Override
    public ResponseEntity<OrderResponseDto> getOrderByNumber(@PathVariable String orderNumber) {
        OrderResponseDto responseDto = orderService.getOrderByNumber(orderNumber);

        return ResponseEntity.ok(responseDto);
    }

    @Operation(
            summary = "Get all orders",
            description = "Retrieves a list of all orders in the system."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of orders retrieved successfully"
                    )
            }
    )
    @GetMapping
    @Override
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        List<OrderResponseDto> orders = orderService.getAllOrders();

        return ResponseEntity.ok(orders);
    }

    @Operation(
            summary = "Get orders by status",
            description = "Retrieves all orders with a specific status (PENDING, APPROVED, ON_HOLD, REJECTED)."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of orders with specified status"
                    )
            }
    )
    @GetMapping("/status/{status}")
    @Override
    public ResponseEntity<List<OrderResponseDto>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<OrderResponseDto> orders = orderService.getOrdersByStatus(status);

        return ResponseEntity.ok(orders);
    }
}
