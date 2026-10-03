#!/bin/bash
# RideLink - Launch Microservices with MongoDB Atlas
set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

if [ -f "$DIR/.env" ]; then
    echo "Loading MongoDB Atlas configuration from .env..."
    export $(grep -v '^#' "$DIR/.env" | xargs)
fi

echo "=========================================================="
echo " Starting RideLink Microservices with MongoDB Atlas"
echo "=========================================================="
echo "MongoDB Atlas Base URI configured."
echo "Account DB:   ridelink_account_db"
echo "Driver DB:    ridelink_driver_db"
echo "Ride DB:      ridelink_ride_db"
echo "Fare DB:      ridelink_fare_payment_db"
echo "=========================================================="

case "$1" in
    account)
        echo "Starting Account Service (:8081)..."
        cd "$DIR/account-service" && mvn spring-boot:run
        ;;
    driver)
        echo "Starting Driver & Vehicle Service (:8082)..."
        cd "$DIR/driver-vehicle-service" && mvn spring-boot:run
        ;;
    ride)
        echo "Starting Ride Management Service (:8083)..."
        cd "$DIR/ride-management-service" && mvn spring-boot:run
        ;;
    fare)
        echo "Starting Fare & Payment Service (:8084)..."
        cd "$DIR/fare-payment-service" && mvn spring-boot:run
        ;;
    *)
        echo "Usage: ./start-services-atlas.sh [account|driver|ride|fare]"
        echo ""
        echo "Examples:"
        echo "  ./start-services-atlas.sh account   # Runs Account Service on :8081"
        echo "  ./start-services-atlas.sh driver    # Runs Driver Service on :8082"
        echo "  ./start-services-atlas.sh ride      # Runs Ride Service on :8083"
        echo "  ./start-services-atlas.sh fare      # Runs Fare Service on :8084"
        ;;
esac
