package wkallil.microservice.orderService.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import wkallil.microservice.orderService.dto.requestDto.CreateOrderRequestDto;
import wkallil.microservice.orderService.dto.responseDto.OrderResponseDto;
import wkallil.microservice.orderService.model.OrderStatus;

import java.util.List;

public interface OrderControllerDocs {
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
    ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody CreateOrderRequestDto request);

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
    ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id);

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
    ResponseEntity<OrderResponseDto> getOrderByNumber(@PathVariable String orderNumber);

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
    ResponseEntity<List<OrderResponseDto>> getAllOrders();

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
    ResponseEntity<List<OrderResponseDto>> getOrdersByStatus(@PathVariable OrderStatus status);
}
