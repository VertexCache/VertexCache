pub fn hello_from_sdk() -> String {
    "VertexCache SDK!".to_string()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_hello_from_sdk() {
        assert_eq!(hello_from_sdk(), "VertexCache SDK!");
    }
}