#!/bin/bash

# Reset the built-in Bash timer
SECONDS=0
CONTAINER_NAME="mariadb-local"
DB_NAME="transactionsdb"

echo "Fetching categories from DB..."
mapfile -t categories < <(docker exec -i "$CONTAINER_NAME" mariadb -u sa -psa -NB -e \
    "SELECT CONCAT(category_name, ':', category_type) FROM ${DB_NAME}.budget_configs WHERE username='tester01';")

echo "Generating 100 transactions with realistic business names..."

for i in {1..100}
do
    # 1. Select random category
    rand_cat=${categories[$RANDOM % ${#categories[@]}]}
    cat_name=$(echo "$rand_cat" | cut -d: -f1)
    cat_type=$(echo "$rand_cat" | cut -d: -f2)

    # 2. Assign realistic Business Name based on category
    case $cat_name in
        "bills")
            names=("Electric Corp" "Water Bill" "Cellcom" "Arnona" "Internet Fiber" "Gas Bill") ;;
        "education")
            names=("Technion" "Udemy" "Coursera" "Book Store" "Tuition" "University Sub") ;;
        "groceries")
            names=("Shufersal" "Rami Levy" "Victory" "Tiv Taam" "Super-Pharm" "Bakery") ;;
        "vehicle")
            names=("Paz" "Sonol" "Ten" "Dor Alon" "Garage Haifa" "Car Wash") ;;
        "housing")
            names=("Home Center" "IKEA" "Tambour" "Ace" "Property Management") ;;
        "leisure")
            names=("Unknown Cafe" "Cinema City" "Netflix" "Spotify" "Restaurant Night") ;;
        "trips"|"vacations")
            names=("El Al" "Booking.com" "Airbnb" "Hotel Rome" "Duty Free") ;;
        "income"|"other incomes")
            names=("Salary" "Refund" "Tax Refund" "Bonus" "Side Gig") ;;
        *)
            names=("Misc Shop" "General Store" "Other Payment") ;;
    esac

    # Pick a random name from the sub-list
    txn_name=${names[$RANDOM % ${#names[@]}]}

    # 3. Generate random date (Last 6 months)
    rand_days=$((RANDOM % 180))
    ts=$(date -d "$rand_days days ago" "+%Y-%m-%d %H:%M:%S")

    # 4. Generate random amount
    if [ "$cat_type" == "INCOME" ]; then
        amount=$(awk -v min=1000 -v max=5000 'BEGIN{srand(); print min+rand()*(max-min)}')
    else
        amount=$(awk -v min=10 -v max=500 'BEGIN{srand(); print min+rand()*(max-min)}')
    fi

    # 5. Execute Insert
    docker exec -i "$CONTAINER_NAME" mariadb -u sa -psa -e \
        "INSERT INTO ${DB_NAME}.transactions (username, name, amount, category_type, default_category, timestamp, is_split, system_flag) 
        VALUES ('tester01', '$txn_name', $amount, '$cat_type', '$cat_name', '$ts', 0, 0);"
done

echo "Done in $SECONDS seconds. 100 records inserted."
