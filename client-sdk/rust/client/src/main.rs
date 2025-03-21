use sdk::hello_from_sdk;

fn main() {
    println!("VertexCache Client!");
    println!("{}", hello_from_sdk());
}

#[cfg(test)]
mod tests {
    use sdk::hello_from_sdk;

    #[test]
    fn test_client_calls_sdk() {
        assert_eq!(hello_from_sdk(), "VertexCache SDK!");
    }
}