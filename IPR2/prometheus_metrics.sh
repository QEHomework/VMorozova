#!/bin/bash

# Configuration
METRICS_FILE="/tmp/resource_metrics.prom"
PORT=9100
INTERVAL=5

# Ensure metrics file exists
touch $METRICS_FILE

# Function to collect metrics
collect_metrics() {
    # CPU usage (system-wide)
    CPU_USAGE=$(top -bn1 | grep "Cpu(s)" | sed "s/.*, *\([0-9.]*\)%* id.*/\1/" | awk '{print 100 - $1}')

    # Memory usage
    MEM_TOTAL=$(free -m | awk '/Mem:/ {print $2}')
    MEM_USED=$(free -m | awk '/Mem:/ {print $3}')
    MEM_USAGE_PERCENT=$(echo "scale=2; $MEM_USED * 100 / $MEM_TOTAL" | bc)

    # Disk usage (root partition)
    DISK_USAGE=$(df -h / | awk 'NR==2 {print $5}' | tr -d '%')

    # Write metrics to file
    cat << EOF > $METRICS_FILE
# HELP node_cpu_usage CPU usage percentage
# TYPE node_cpu_usage gauge
node_cpu_usage $CPU_USAGE
# HELP node_memory_usage Memory usage percentage
# TYPE node_memory_usage gauge
node_memory_usage $MEM_USAGE_PERCENT
# HELP node_memory_used Memory used in MB
# TYPE node_memory_used gauge
node_memory_used $MEM_USED
# HELP node_memory_total Total memory in MB
# TYPE node_memory_total gauge
node_memory_total $MEM_TOTAL
# HELP node_disk_usage Disk usage percentage
# TYPE node_disk_usage gauge
node_disk_usage $DISK_USAGE
EOF
}

# Function to serve metrics via HTTP
serve_metrics() {
    while true; do
        collect_metrics
        {
            printf "HTTP/1.1 200 OK\r\n"
            printf "Content-Type: text/plain\r\n"
            printf "\r\n"
            cat $METRICS_FILE
        } | nc -l -p $PORT -q 1
    done
}

# Main execution
case "$1" in
    start)
        echo "Starting resource monitor..."
        serve_metrics &
        echo $! > /tmp/resource_monitor.pid
        ;;
    stop)
        echo "Stopping resource monitor..."
        kill $(cat /tmp/resource_monitor.pid)
        rm -f /tmp/resource_monitor.pid
        ;;
    restart)
        $0 stop
        sleep 1
        $0 start
        ;;
    check)
        collect_metrics
        cat $METRICS_FILE
        ;;
    *)
        echo "Usage: $0 {start|stop|restart|check}"
        exit 1
        ;;
esac

exit 0