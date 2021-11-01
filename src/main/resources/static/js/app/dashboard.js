
$(function ($, e) {
    $.ajax({
        type: "GET",
        url: "/dashboard/data",
        success: function (response) {
            if (response.status==='00') {
                var data = response.data;
                $("#countActiveRiders").html(data.activeRiders);
                $("#pendingDeliveriesCount").html(data.pendingDeliveries);
                $("#assignedDeliveriesCount").html(data.assignedOrders);
                $("#completedDeliveries").html(data.completedOrders);
                $("#declinedDeliveries").html(data.declinedDeliveries);

            }
        }
    });

});

