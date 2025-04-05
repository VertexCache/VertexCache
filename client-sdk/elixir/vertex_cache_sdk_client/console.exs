Logger.configure(level: :info)

defmodule VertexCacheConsole do
  alias VertexCacheSDK.Transport.TcpClient
  alias VertexCacheSDK.Protocol.Command

  def run do
    config_path = "config/.env"

    # ✅ Clear screen
    IO.write(IO.ANSI.clear() <> IO.ANSI.home())

    if File.exists?(config_path) do
      DotenvParser.load_file(config_path)

      print_banner(config_path)

      case TcpClient.connect() do
        {:ok, client} ->
          loop(client)

        {:error, reason} ->
          IO.puts("Failed to connect: #{inspect(reason)}")
      end
    else
      IO.puts("ERROR: .env file not found at #{config_path}")
    end
  end

  defp print_banner(config_path) do
    IO.puts("""
    VertexCache Elixir Client Console:
      Host: #{System.get_env("server_host")}
      Port: #{System.get_env("server_port")}
      Message Layer Encryption Enabled: #{yes_no("enable_encrypt_message")}
      Transport Layer Encryption Enabled: #{yes_no("enable_encrypt_transport")}
      Transport Layer Verify Certificate: #{yes_no("enable_verify_certificate")}
    """)
  end

  defp yes_no(var), do: if(System.get_env(var) == "true", do: "Yes", else: "No")

  defp loop(client) do
    prompt = "VertexCache Console, #{System.get_env("server_host")}:#{System.get_env("server_port")}> "

    input =
      IO.gets(prompt)
      |> to_string()
      |> String.trim()

    cond do
      input == "exit" or input == "quit" ->
        IO.puts("Closing connection...")
        TcpClient.close(client)
        System.halt(0)

      input == "" ->
        loop(client)

      true ->
        command = Command.new(input) |> Command.to_wire()

        case TcpClient.send_command(client, command) do
          {:ok, response} ->
            IO.puts(response)

          {:error, reason} ->
            IO.puts("Error: #{inspect(reason)}")
        end

        loop(client)
    end
  end
end

VertexCacheConsole.run()
